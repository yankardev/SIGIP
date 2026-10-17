package pe.edu.cibertec.sigip.reportservice.client;

import java.math.BigDecimal;
import java.util.Map;

/** Respuesta de GET /api/v1/pedidos/resumen (sales-service). */
public record ResumenVentas(long totalPedidos, Map<String, Long> pedidosPorEstado, BigDecimal ventasConfirmadas) {
}
