package pe.edu.cibertec.sigip.catalogservice.optimizacion;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;
import pe.edu.cibertec.sigip.catalogservice.dto.ServicioImpresionResponse;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;
import pe.edu.cibertec.sigip.catalogservice.repository.CategoriaRepository;
import pe.edu.cibertec.sigip.catalogservice.repository.ServicioImpresionRepository;
import pe.edu.cibertec.sigip.catalogservice.service.ServicioImpresionService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Medición reproducible de la optimización del catálogo (Tema 3 - Optimización de arquitecturas).
 * Compara la consulta del catálogo en tres escenarios con los mismos datos:
 * <ol>
 *   <li>ANTES: findAll() con categoría LAZY (problema N+1).</li>
 *   <li>DESPUÉS 1: consulta con JOIN FETCH (una sola consulta SQL).</li>
 *   <li>DESPUÉS 2: JOIN FETCH + caché Caffeine (las lecturas repetidas no tocan la BD).</li>
 * </ol>
 * Cuenta las consultas SQL con las estadísticas de Hibernate y mide el tiempo promedio.
 * El resultado se guarda en target/optimizacion-catalogo.md (también lo publica el pipeline de CI).
 */
@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@ActiveProfiles("test")
class OptimizacionCatalogoTest {

    private static final int CATEGORIAS = 20;
    private static final int SERVICIOS = 200;
    private static final int REPETICIONES = 50;

    @Autowired
    private ServicioImpresionRepository servicioRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ServicioImpresionService servicioService;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private CacheManager cacheManager;

    private Statistics stats;

    @BeforeEach
    void cargarDatos() {
        servicioRepository.deleteAll();
        categoriaRepository.deleteAll();
        cacheManager.getCacheNames().forEach(n -> cacheManager.getCache(n).clear());
        for (int c = 1; c <= CATEGORIAS; c++) {
            Categoria categoria = categoriaRepository.save(
                    Categoria.builder().nombre("Categoria " + c).activo(true).build());
            for (int s = 1; s <= SERVICIOS / CATEGORIAS; s++) {
                servicioRepository.save(ServicioImpresion.builder()
                        .nombre("Servicio " + c + "-" + s).precioBase(new BigDecimal("10.00"))
                        .requiereCotizacion(false).activo(true).categoria(categoria).build());
            }
        }
        stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }

    @Test
    void compararAntesYDespues() throws IOException {
        Medicion antes = medir(() -> transactionTemplate.execute(tx ->
                servicioRepository.findAll().stream().map(ServicioImpresionResponse::from).toList()));

        Medicion joinFetch = medir(() -> transactionTemplate.execute(tx ->
                servicioRepository.listarConCategoria().stream().map(ServicioImpresionResponse::from).toList()));

        Medicion conCache = medir(() -> servicioService.listar());

        String reporte = """
                # Optimización del catálogo — resultados medidos

                Datos: %d servicios en %d categorías. Promedio de %d ejecuciones por escenario.

                | Escenario | Consultas SQL por listado | Tiempo promedio (ms) | Registros |
                |---|---:|---:|---:|
                | Antes: findAll() + categoría LAZY (N+1) | %.1f | %.3f | %d |
                | Después 1: JOIN FETCH | %.1f | %.3f | %d |
                | Después 2: JOIN FETCH + caché Caffeine | %.2f | %.3f | %d |

                Reducción de consultas: de %.0f a %.0f por listado (y prácticamente 0 con caché).
                Mejora de tiempo con caché frente al escenario inicial: %.1fx más rápido.
                """.formatted(SERVICIOS, CATEGORIAS, REPETICIONES,
                antes.consultas, antes.ms, antes.registros,
                joinFetch.consultas, joinFetch.ms, joinFetch.registros,
                conCache.consultas, conCache.ms, conCache.registros,
                antes.consultas, joinFetch.consultas,
                antes.ms / Math.max(conCache.ms, 0.0001));

        System.out.println(reporte);
        Files.writeString(Path.of("target", "optimizacion-catalogo.md"), reporte);

        assertThat(antes.registros).isEqualTo(SERVICIOS);
        assertThat(joinFetch.registros).isEqualTo(SERVICIOS);
        assertThat(conCache.registros).isEqualTo(SERVICIOS);
        assertThat(antes.consultas).isGreaterThan(CATEGORIAS);   // 1 + N consultas
        assertThat(joinFetch.consultas).isEqualTo(1.0);           // una sola consulta
        assertThat(conCache.consultas).isLessThan(0.1);           // solo la primera llamada consulta la BD
    }

    private Medicion medir(Supplier<List<ServicioImpresionResponse>> accion) {
        accion.get(); // calentamiento (JIT, pool de conexiones)
        stats.clear();
        int registros = 0;
        long inicio = System.nanoTime();
        for (int i = 0; i < REPETICIONES; i++) {
            registros = accion.get().size();
        }
        double ms = (System.nanoTime() - inicio) / 1_000_000.0 / REPETICIONES;
        double consultas = (double) stats.getPrepareStatementCount() / REPETICIONES;
        return new Medicion(consultas, ms, registros);
    }

    private record Medicion(double consultas, double ms, int registros) {
    }
}
