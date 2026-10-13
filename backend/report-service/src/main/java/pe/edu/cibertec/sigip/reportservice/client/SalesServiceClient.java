package pe.edu.cibertec.sigip.reportservice.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.cibertec.sigip.common.error.ServicioNoDisponibleException;

@FeignClient(name = "sales-service", fallbackFactory = SalesServiceClient.Fallback.class)
public interface SalesServiceClient {

    @GetMapping("/api/v1/pedidos/resumen")
    ResumenVentas resumen();

    @Component
    class Fallback implements FallbackFactory<SalesServiceClient> {
        @Override
        public SalesServiceClient create(Throwable causa) {
            return () -> {
                throw new ServicioNoDisponibleException("El servicio de ventas no está disponible en este momento");
            };
        }
    }
}
