package pe.edu.cibertec.sigip.reportservice.dto;

import pe.edu.cibertec.sigip.reportservice.client.InsumoCritico;
import pe.edu.cibertec.sigip.reportservice.client.ResumenVentas;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tablero del administrador. Si un servicio no responde, su sección llega vacía y se explica en
 * {@code avisos}: el tablero se degrada en lugar de fallar por completo.
 */
public record DashboardResponse(
        LocalDateTime generadoEn,
        ResumenVentas ventas,
        long pedidosPorAtender,
        long pedidosEnTaller,
        List<InsumoCritico> insumosBajoStock,
        List<String> avisos
) {
}
