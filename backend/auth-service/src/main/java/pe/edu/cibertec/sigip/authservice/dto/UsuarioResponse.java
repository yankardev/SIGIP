package pe.edu.cibertec.sigip.authservice.dto;

import pe.edu.cibertec.sigip.authservice.model.Usuario;

import java.util.List;

public record UsuarioResponse(
        Integer idusuario,
        String nomusuario,
        String email,
        String nombres,
        String apellidos,
        Boolean activo,
        List<String> roles
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdusuario(),
                usuario.getNomusuario(),
                usuario.getEmail(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getActivo(),
                usuario.getRoles().stream()
                        .map(rol -> rol.getNomrol())
                        .sorted()
                        .toList()
        );
    }
}
