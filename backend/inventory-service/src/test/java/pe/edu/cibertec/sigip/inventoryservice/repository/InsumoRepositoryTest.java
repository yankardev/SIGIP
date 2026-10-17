package pe.edu.cibertec.sigip.inventoryservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.cibertec.sigip.inventoryservice.model.Insumo;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Pruebas de la capa de acceso a datos de insumos: insertar, actualizar, eliminar y listar. */
@DataJpaTest
@ActiveProfiles("test")
class InsumoRepositoryTest {

    @Autowired
    private InsumoRepository repository;

    @Test
    void debeInsertarInsumo() {
        Insumo guardado = repository.save(insumo("PAP-001", "100", "20"));
        assertThat(guardado.getIdinsumo()).isNotNull();
        assertThat(repository.existsByCodigoIgnoreCase("pap-001")).isTrue();
    }

    @Test
    void debeActualizarInsumo() {
        Insumo guardado = repository.saveAndFlush(insumo("PAP-001", "100", "20"));
        guardado.setStockActual(new BigDecimal("80"));
        repository.saveAndFlush(guardado);

        assertThat(repository.findById(guardado.getIdinsumo())).get()
                .extracting(Insumo::getStockActual).satisfies(s -> assertThat(s).isEqualByComparingTo("80"));
    }

    @Test
    void debeEliminarInsumo() {
        Insumo guardado = repository.saveAndFlush(insumo("PAP-001", "100", "20"));
        repository.deleteById(guardado.getIdinsumo());
        repository.flush();

        assertThat(repository.findById(guardado.getIdinsumo())).isEmpty();
    }

    @Test
    void debeListarYDetectarBajoStock() {
        repository.save(insumo("PAP-001", "100", "20"));
        repository.save(insumo("TIN-001", "5", "10"));
        repository.save(insumo("VIN-001", "10", "10"));

        assertThat(repository.findAll()).hasSize(3);
        assertThat(repository.bajoStock()).extracting(Insumo::getCodigo).containsExactlyInAnyOrder("TIN-001", "VIN-001");
    }

    private Insumo insumo(String codigo, String stock, String minimo) {
        return Insumo.builder().codigo(codigo).nombre("Insumo " + codigo).unidadMedida("UND")
                .stockActual(new BigDecimal(stock)).stockMinimo(new BigDecimal(minimo)).activo(true).build();
    }
}
