package pe.edu.cibertec.sigip.productionservice.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.cibertec.sigip.common.error.RecursoNoEncontradoException;
import pe.edu.cibertec.sigip.common.error.ReglaNegocioException;
import pe.edu.cibertec.sigip.common.error.ServicioNoDisponibleException;

/**
 * Comunicación síncrona con sales-service: validar el pedido antes de producir
 * y mantener sincronizado su estado (EN_PRODUCCION, TERMINADO).
 */
@FeignClient(name = "sales-service", fallbackFactory = SalesServiceClient.Fallback.class)
public interface SalesServiceClient {

    @GetMapping("/api/v1/pedidos/{id}")
    PedidoResumen obtenerPedido(@PathVariable("id") Integer id);

    @PatchMapping("/api/v1/pedidos/{id}/estado")
    PedidoResumen cambiarEstado(@PathVariable("id") Integer id, @RequestBody CambioEstadoPedido cambio);

    @Component
    class Fallback implements FallbackFactory<SalesServiceClient> {
        @Override
        public SalesServiceClient create(Throwable causa) {
            return new SalesServiceClient() {
                @Override
                public PedidoResumen obtenerPedido(Integer id) {
                    throw traducir(causa, id);
                }

                @Override
                public PedidoResumen cambiarEstado(Integer id, CambioEstadoPedido cambio) {
                    throw traducir(causa, id);
                }
            };
        }

        private static RuntimeException traducir(Throwable causa, Integer id) {
            if (causa instanceof FeignException.NotFound) {
                return new RecursoNoEncontradoException("El pedido " + id + " no existe");
            }
            if (causa instanceof FeignException.Conflict) {
                return new ReglaNegocioException("El pedido " + id + " no admite ese cambio de estado");
            }
            return new ServicioNoDisponibleException("El servicio de ventas no está disponible en este momento");
        }
    }
}
