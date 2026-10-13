package pe.edu.cibertec.sigip.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.authservice.dto.RegistroUsuarioRequest;
import pe.edu.cibertec.sigip.authservice.dto.UsuarioAdminRequest;
import pe.edu.cibertec.sigip.authservice.dto.UsuarioResponse;
import pe.edu.cibertec.sigip.authservice.model.Rol;
import pe.edu.cibertec.sigip.authservice.model.Usuario;
import pe.edu.cibertec.sigip.authservice.repository.RolRepository;
import pe.edu.cibertec.sigip.authservice.repository.UsuarioRepository;
import pe.edu.cibertec.sigip.common.error.RecursoNoEncontradoException;
import pe.edu.cibertec.sigip.common.error.ReglaNegocioException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final int PASSWORD_MINIMO = 8;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /** Registro público: siempre crea un CLIENTE, nunca acepta roles enviados por el navegador. */
    @Transactional
    public UsuarioResponse registrarCliente(RegistroUsuarioRequest request) {
        validarUnicidad(request.nomusuario(), request.email(), null);

        Usuario usuario = Usuario.builder()
                .nomusuario(request.nomusuario())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .activo(true)
                .build();
        usuario.getRoles().add(buscarRol("CLIENTE"));

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Integer id) {
        return UsuarioResponse.from(buscar(id));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorNombre(String nomusuario) {
        return usuarioRepository.findByNomusuario(nomusuario)
                .map(UsuarioResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    @Transactional
    public UsuarioResponse registrarPorAdmin(UsuarioAdminRequest request) {
        validarUnicidad(request.nomusuario(), request.email(), null);
        if (request.password() == null || request.password().length() < PASSWORD_MINIMO) {
            throw new ReglaNegocioException("La contraseña debe tener al menos " + PASSWORD_MINIMO + " caracteres");
        }

        Usuario usuario = Usuario.builder()
                .nomusuario(request.nomusuario())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .activo(request.activo())
                .roles(resolverRoles(request.roles()))
                .build();

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Integer id, UsuarioAdminRequest request) {
        Usuario usuario = buscar(id);
        validarUnicidad(request.nomusuario(), request.email(), usuario);

        usuario.setNomusuario(request.nomusuario());
        usuario.setEmail(request.email());
        usuario.setNombres(request.nombres());
        usuario.setApellidos(request.apellidos());
        usuario.setActivo(request.activo());
        usuario.setRoles(resolverRoles(request.roles()));
        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < PASSWORD_MINIMO) {
                throw new ReglaNegocioException("La contraseña debe tener al menos " + PASSWORD_MINIMO + " caracteres");
            }
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Integer id) {
        usuarioRepository.delete(buscar(id));
    }

    private Usuario buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private void validarUnicidad(String nomusuario, String email, Usuario actual) {
        boolean cambiaNombre = actual == null || !actual.getNomusuario().equalsIgnoreCase(nomusuario);
        boolean cambiaEmail = actual == null || !actual.getEmail().equalsIgnoreCase(email);
        if (cambiaNombre && usuarioRepository.existsByNomusuario(nomusuario)) {
            throw new ReglaNegocioException("El nombre de usuario ya está registrado");
        }
        if (cambiaEmail && usuarioRepository.existsByEmail(email)) {
            throw new ReglaNegocioException("El correo ya está registrado");
        }
    }

    private Set<Rol> resolverRoles(Set<String> nombres) {
        Set<Rol> roles = new HashSet<>();
        for (String nombre : nombres) {
            roles.add(buscarRol(nombre.toUpperCase()));
        }
        return roles;
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNomrol(nombre)
                .orElseThrow(() -> new ReglaNegocioException("El rol " + nombre + " no existe"));
    }
}
