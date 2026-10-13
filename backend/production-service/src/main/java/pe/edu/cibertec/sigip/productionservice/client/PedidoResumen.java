package pe.edu.cibertec.sigip.productionservice.client;

/** Datos del pedido que necesita production-service (contrato con sales-service). */
public record PedidoResumen(Integer idpedido, String cliente, String nombreServicio, Integer cantidad, String estado) {
}
