package pe.edu.cibertec.sigip.inventoryservice.dto;

import pe.edu.cibertec.sigip.inventoryservice.model.MovimientoInventario;
import pe.edu.cibertec.sigip.inventoryservice.model.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoResponse(
        Integer idmovimiento,
        Integer idinsumo,
        TipoMovimiento tipo,
        BigDecimal cantidad,
        BigDecimal stockResultante,
        String motivo,
        Integer idpedido,
        String usuario,
        LocalDateTime fecha
) {
    public static MovimientoResponse from(MovimientoInventario m) {
        return new MovimientoResponse(m.getIdmovimiento(), m.getInsumo().getIdinsumo(), m.getTipo(), m.getCantidad(),
                m.getStockResultante(), m.getMotivo(), m.getIdpedido(), m.getUsuario(), m.getFecha());
    }
}
