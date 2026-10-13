package pe.edu.cibertec.sigip.inventoryservice.dto;

import jakarta.validation.constraints.*;
import pe.edu.cibertec.sigip.inventoryservice.model.TipoMovimiento;

import java.math.BigDecimal;

public record MovimientoRequest(
        @NotNull(message = "El tipo es obligatorio (ENTRADA o SALIDA)")
        TipoMovimiento tipo,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor que cero")
        BigDecimal cantidad,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(max = 200, message = "El motivo no puede superar 200 caracteres")
        String motivo,

        Integer idpedido
) {
}
