package pe.edu.cibertec.sigip.authservice.dto;

public record LoginResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
}
