package pe.edu.cibertec.sigip.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/** Usuario autenticado desde JWT: distingue administración, personal interno y clientes. */
public record UsuarioActual(String nombre, boolean esAdmin, boolean esPersonalInterno) {

    private static final Set<String> ROLES_INTERNOS = Set.of(
            "ROLE_ADMIN", "ROLE_VENTAS", "ROLE_CAJA", "ROLE_DISENO", "ROLE_PRODUCCION", "ROLE_ALMACEN"
    );

    /** Compatibilidad con código y pruebas que solo necesitan identificar ADMIN. */
    public UsuarioActual(String nombre, boolean esAdmin) {
        this(nombre, esAdmin, esAdmin);
    }

    public static UsuarioActual de(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());
        boolean admin = roles.contains("ROLE_ADMIN");
        boolean interno = roles.stream().anyMatch(ROLES_INTERNOS::contains);
        return new UsuarioActual(authentication.getName(), admin, interno);
    }
}
