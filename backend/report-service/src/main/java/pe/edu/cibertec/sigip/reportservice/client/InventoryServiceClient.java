package pe.edu.cibertec.sigip.reportservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.cibertec.sigip.common.error.ServicioNoDisponibleException;

import java.util.List;

@FeignClient(name = "inventory-service", fallbackFactory = InventoryServiceClient.Fallback.class)
public interface InventoryServiceClient {

    @GetMapping("/api/v1/insumos/bajo-stock")
    List<InsumoCritico> bajoStock();

    @Component
    class Fallback implements FallbackFactory<InventoryServiceClient> {
        @Override
        public InventoryServiceClient create(Throwable causa) {
            return () -> {
                throw new ServicioNoDisponibleException("El servicio de inventario no está disponible en este momento");
            };
        }
    }
}
