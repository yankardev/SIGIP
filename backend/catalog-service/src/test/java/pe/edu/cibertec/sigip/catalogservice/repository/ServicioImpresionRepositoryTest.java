package pe.edu.cibertec.sigip.catalogservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Pruebas de la capa de acceso a datos: insertar, actualizar, eliminar y listar servicios de impresión. */
@DataJpaTest
@ActiveProfiles("test")
class ServicioImpresionRepositoryTest {

    @Autowired
    private ServicioImpresionRepository servicioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = categoriaRepository.save(Categoria.builder()
                .nombre("Impresión")
                .descripcion("Pruebas")
                .activo(true)
                .build());
    }

    @Test
    void debeInsertarServicio() {
        ServicioImpresion servicio = servicioRepository.save(servicio("Volantes"));

        assertThat(servicio.getIdservicio()).isNotNull();
    }

    @Test
    void debeListarServicios() {
        servicioRepository.save(servicio("Volantes"));
        servicioRepository.save(servicio("Tarjetas"));

        assertThat(servicioRepository.findAll()).hasSize(2);
        assertThat(servicioRepository.listarConCategoria())
                .extracting(ServicioImpresion::getNombre)
                .containsExactly("Tarjetas", "Volantes");
    }

    @Test
    void debeActualizarServicio() {
        ServicioImpresion servicio = servicioRepository.saveAndFlush(servicio("Volantes"));

        servicio.setNombre("Volantes A5");
        servicio.setPrecioBase(new BigDecimal("120.00"));
        servicioRepository.saveAndFlush(servicio);

        ServicioImpresion actualizado = servicioRepository.findById(servicio.getIdservicio()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo("Volantes A5");
        assertThat(actualizado.getPrecioBase()).isEqualByComparingTo("120.00");
    }

    @Test
    void debeEliminarServicio() {
        ServicioImpresion servicio = servicioRepository.saveAndFlush(servicio("Volantes"));

        servicioRepository.deleteById(servicio.getIdservicio());
        servicioRepository.flush();

        assertThat(servicioRepository.findById(servicio.getIdservicio())).isEmpty();
    }

    private ServicioImpresion servicio(String nombre) {
        return ServicioImpresion.builder()
                .nombre(nombre)
                .descripcion("Servicio de prueba")
                .precioBase(new BigDecimal("100.00"))
                .requiereCotizacion(false)
                .activo(true)
                .categoria(categoria)
                .build();
    }
}
