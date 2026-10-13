package pe.edu.cibertec.sigip.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Alta o edición de usuario por un administrador.
 * En la edición la contraseña es opcional: si llega vacía se conserva la anterior.
 */
public record UsuarioAdminRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
        String nomusuario,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String email,

        @Size(max = 100, message = "La contraseña no puede superar 100 caracteres")
        String password,

        @NotBlank(message = "Los nombres son obligatorios")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        @NotNull(message = "Debe indicar si el usuario está activo")
        Boolean activo,

        @NotEmpty(message = "Debe asignar al menos un rol")
        Set<String> roles
) {
}
