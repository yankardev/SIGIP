package pe.edu.cibertec.sigip.salesservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Precio comercial definido por el personal para una solicitud que requiere cotización. */
public record CotizacionRequest(
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor que cero")
        BigDecimal precioUnitario,

        @Size(max = 300, message = "La observación no puede superar 300 caracteres")
        String observacion
) {
}
