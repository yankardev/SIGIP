package pe.edu.cibertec.sigip.productionservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.cibertec.sigip.productionservice.model.EstadoOrden;
import pe.edu.cibertec.sigip.productionservice.model.OrdenProduccion;
import pe.edu.cibertec.sigip.productionservice.model.Prioridad;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas de la capa de acceso a datos de órdenes: insertar, actualizar, eliminar y listar. */
@DataJpaTest
@ActiveProfiles("test")
class OrdenProduccionRepositoryTest {

    @Autowired
    private OrdenProduccionRepository repository;

    @Test
    void debeInsertarOrden() {
        assertThat(repository.save(orden(1, Prioridad.MEDIA)).getIdorden()).isNotNull();
    }

    @Test
    void debeActualizarOrden() {
        OrdenProduccion guardada = repository.saveAndFlush(orden(1, Prioridad.BAJA));
        guardada.setEstado(EstadoOrden.EN_PROCESO);
        guardada.setResponsable("Operario 1");
        repository.saveAndFlush(guardada);

        OrdenProduccion actualizada = repository.findById(guardada.getIdorden()).orElseThrow();
        assertThat(actualizada.getEstado()).isEqualTo(EstadoOrden.EN_PROCESO);
        assertThat(actualizada.getResponsable()).isEqualTo("Operario 1");
    }

    @Test
    void debeEliminarOrden() {
        OrdenProduccion guardada = repository.saveAndFlush(orden(1, Prioridad.BAJA));
        repository.deleteById(guardada.getIdorden());
        repository.flush();

        assertThat(repository.findById(guardada.getIdorden())).isEmpty();
    }

    @Test
    void debeListarPorEstadoYPrioridad() {
        repository.save(orden(1, Prioridad.BAJA));
        repository.save(orden(2, Prioridad.ALTA));
        repository.save(orden(3, Prioridad.MEDIA));

        assertThat(repository.findAll()).hasSize(3);
        assertThat(repository.colaPorEstado(EstadoOrden.PENDIENTE))
                .extracting(OrdenProduccion::getPrioridad)
                .containsExactly(Prioridad.ALTA, Prioridad.MEDIA, Prioridad.BAJA);
    }

    @Test
    void noPermiteDosOrdenesParaElMismoPedido() {
        repository.saveAndFlush(orden(5, Prioridad.BAJA));
        assertThatThrownBy(() -> repository.saveAndFlush(orden(5, Prioridad.ALTA)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private OrdenProduccion orden(int idpedido, Prioridad prioridad) {
        return OrdenProduccion.builder().idpedido(idpedido).estado(EstadoOrden.PENDIENTE).prioridad(prioridad)
                .fechaRegistro(LocalDateTime.now()).build();
    }
}
