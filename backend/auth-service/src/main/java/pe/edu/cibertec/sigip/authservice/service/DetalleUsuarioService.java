package pe.edu.cibertec.sigip.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.sigip.authservice.model.Usuario;
import pe.edu.cibertec.sigip.authservice.repository.UsuarioRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNomusuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new DisabledException("Usuario inactivo");
        }

        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNomrol()))
                .map(GrantedAuthority.class::cast)
                .toList();

        return User.builder()
                .username(usuario.getNomusuario())
                .password(usuario.getPassword())
                .authorities(authorities)
                .disabled(!usuario.getActivo())
                .build();
    }
}
