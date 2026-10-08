package pe.edu.cibertec.sigip.catalogservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ServicioImpresionRepositoryTest {

    @Autowired
    private ServicioImpresionRepository servicioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

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
    }

    @Test
    void debeActualizarServicio() {
        ServicioImpresion servicio = servicioRepository.saveAndFlush(servicio("Volantes"));

        servicio.setNombre("Volantes A5");
        servicioRepository.saveAndFlush(servicio);

        assertThat(servicioRepository.findById(servicio.getIdservicio()))
                .get()
                .extracting(ServicioImpresion::getNombre)
                .isEqualTo("Volantes A5");
    }

    @Test
    void debeEliminarServicio() {
        ServicioImpresion servicio = servicioRepository.saveAndFlush(servicio("Volantes"));

        servicioRepository.deleteById(servicio.getIdservicio());
        servicioRepository.flush();

        assertThat(servicioRepository.findById(servicio.getIdservicio())).isEmpty();
    }

    private ServicioImpresion servicio(String nombre) {
        Categoria categoria = categoriaRepository.save(
                Categoria.builder()
                        .nombre("Impresión")
                        .descripcion("Pruebas")
                        .activo(true)
                        .build()
        );

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
