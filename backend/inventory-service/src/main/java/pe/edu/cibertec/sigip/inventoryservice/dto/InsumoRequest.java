package pe.edu.cibertec.sigip.inventoryservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record InsumoRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "El código no puede superar 30 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @NotBlank(message = "La unidad de medida es obligatoria")
        @Size(max = 30, message = "La unidad de medida no puede superar 30 caracteres")
        String unidadMedida,

        @NotNull(message = "El stock inicial es obligatorio")
        @DecimalMin(value = "0", message = "El stock no puede ser negativo")
        BigDecimal stockActual,

        @NotNull(message = "El stock mínimo es obligatorio")
        @DecimalMin(value = "0", message = "El stock mínimo no puede ser negativo")
        BigDecimal stockMinimo,

        Boolean activo
) {
}
