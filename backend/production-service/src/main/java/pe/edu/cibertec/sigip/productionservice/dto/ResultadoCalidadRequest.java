package pe.edu.cibertec.sigip.productionservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResultadoCalidadRequest(
        @NotNull(message = "Indique si el trabajo aprobó el control de calidad")
        Boolean aprobado,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observacion
) {
}
