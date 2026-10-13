package pe.edu.cibertec.sigip.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.authservice.dto.RegistroUsuarioRequest;
import pe.edu.cibertec.sigip.authservice.dto.UsuarioResponse;
import pe.edu.cibertec.sigip.authservice.model.Rol;
import pe.edu.cibertec.sigip.authservice.model.Usuario;
import pe.edu.cibertec.sigip.authservice.repository.RolRepository;
import pe.edu.cibertec.sigip.authservice.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse registrarCliente(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsByNomusuario(request.nomusuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Rol rolCliente = rolRepository.findByNomrol("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("El rol CLIENTE no está configurado"));

        Usuario usuario = Usuario.builder()
                .nomusuario(request.nomusuario())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .activo(true)
                .build();

        usuario.getRoles().add(rolCliente);

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }
}
