package pe.edu.cibertec.sigip.catalogservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;
import pe.edu.cibertec.sigip.catalogservice.repository.CategoriaRepository;
import pe.edu.cibertec.sigip.catalogservice.repository.ServicioImpresionRepository;

import java.math.BigDecimal;

/** Carga un catálogo de ejemplo solo si la base está vacía (útil para la demo y el video). */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sigip.catalogo.datos-demo", havingValue = "true", matchIfMissing = true)
public class DatosDemoCatalogo implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ServicioImpresionRepository servicioRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (categoriaRepository.count() > 0) {
            return;
        }
        Categoria digital = categoria("Impresión digital", "Trabajos de pequeño formato en papel");
        Categoria granFormato = categoria("Gran formato", "Banners, gigantografías y viniles");
        Categoria especiales = categoria("Trabajos especiales", "Pedidos personalizados que requieren cotización");

        servicio("Volantes A5 (millar)", "Papel couché 115 g, full color", "95.00", false, digital);
        servicio("Tarjetas personales (millar)", "Cartulina 300 g, plastificado mate", "60.00", false, digital);
        servicio("Stickers troquelados (ciento)", "Vinil adhesivo con corte", "45.00", false, digital);
        servicio("Banner 1 x 2 m", "Lona 13 oz con ojales", "70.00", false, granFormato);
        servicio("Gigantografía m2", "Lona front impresa a alta resolución", "28.00", false, granFormato);
        servicio("Packaging personalizado", "Cajas y empaques a medida, precio por unidad", "4.50", true, especiales);
    }

    private Categoria categoria(String nombre, String descripcion) {
        return categoriaRepository.save(Categoria.builder().nombre(nombre).descripcion(descripcion).activo(true).build());
    }

    private void servicio(String nombre, String descripcion, String precio, boolean cotizacion, Categoria categoria) {
        servicioRepository.save(ServicioImpresion.builder()
                .nombre(nombre).descripcion(descripcion).precioBase(new BigDecimal(precio))
                .requiereCotizacion(cotizacion).activo(true).categoria(categoria).build());
    }
}
