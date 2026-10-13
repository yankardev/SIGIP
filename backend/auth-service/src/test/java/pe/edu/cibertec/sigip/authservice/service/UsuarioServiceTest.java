package pe.edu.cibertec.sigip.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.cibertec.sigip.authservice.dto.RegistroUsuarioRequest;
import pe.edu.cibertec.sigip.authservice.model.Rol;
import pe.edu.cibertec.sigip.authservice.model.Usuario;
import pe.edu.cibertec.sigip.authservice.repository.RolRepository;
import pe.edu.cibertec.sigip.authservice.repository.UsuarioRepository;
import pe.edu.cibertec.sigip.common.error.ReglaNegocioException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de la lógica de registro (sin base de datos, con Mockito). */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private RegistroUsuarioRequest request;

    @BeforeEach
    void setUp() {
        request = new RegistroUsuarioRequest("cliente01", "c01@sigip.test", "Cliente123", "Ana", "Pérez");
    }

    @Test
    void registraClienteConPasswordCifradaYRolCliente() {
        when(rolRepository.findByNomrol("CLIENTE")).thenReturn(Optional.of(new Rol(1, "CLIENTE")));
        when(passwordEncoder.encode("Cliente123")).thenReturn("$2a$hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        var respuesta = usuarioService.registrarCliente(request);

        assertThat(respuesta.roles()).containsExactly("CLIENTE");
        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(u -> u.getPassword().equals("$2a$hash")));
    }

    @Test
    void rechazaUsuarioDuplicado() {
        when(usuarioRepository.existsByNomusuario("cliente01")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrarCliente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("usuario ya está registrado");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void rechazaCorreoDuplicado() {
        when(usuarioRepository.existsByEmail("c01@sigip.test")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrarCliente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("correo");
    }
}
