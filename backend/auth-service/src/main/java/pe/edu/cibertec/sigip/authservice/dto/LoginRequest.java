package pe.edu.cibertec.sigip.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El usuario es obligatorio")
        String nomusuario,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
