package pe.edu.cibertec.sigip.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/** Usuario autenticado tomado del token JWT: nombre y si tiene rol ADMIN. */
public record UsuarioActual(String nombre, boolean esAdmin) {

    public static UsuarioActual de(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        return new UsuarioActual(authentication.getName(), admin);
    }
}
