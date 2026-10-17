package pe.edu.cibertec.sigip.reportservice.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.cibertec.sigip.common.error.ServicioNoDisponibleException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FallbackTest {

    @Test
    @DisplayName("Los fallbacks traducen la caída de un servicio en 503 controlado")
    void fallbacks() {
        var sales = new SalesServiceClient.Fallback().create(new RuntimeException("timeout"));
        var inventory = new InventoryServiceClient.Fallback().create(new RuntimeException("timeout"));

        assertThatThrownBy(sales::resumen).isInstanceOf(ServicioNoDisponibleException.class);
        assertThatThrownBy(inventory::bajoStock).isInstanceOf(ServicioNoDisponibleException.class);
    }
}
