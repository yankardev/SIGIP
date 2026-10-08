package pe.edu.cibertec.sigip.authservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.cibertec.sigip.authservice.model.Usuario;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void debeInsertarYListarUsuario() {
        Usuario usuario = Usuario.builder()
                .nomusuario("cliente01")
                .email("cliente01@sigip.test")
                .password("hash")
                .nombres("Cliente")
                .apellidos("Prueba")
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        assertThat(guardado.getIdusuario()).isNotNull();
        assertThat(usuarioRepository.findAll()).hasSize(1);
        assertThat(usuarioRepository.findByNomusuario("cliente01")).isPresent();
    }

    @Test
    void debeActualizarUsuario() {
        Usuario usuario = Usuario.builder()
                .nomusuario("cliente02")
                .email("cliente02@sigip.test")
                .password("hash")
                .nombres("Cliente")
                .apellidos("Dos")
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.saveAndFlush(usuario);
        guardado.setNombres("Cliente Actualizado");
        usuarioRepository.saveAndFlush(guardado);

        Usuario actualizado = usuarioRepository
                .findByNomusuario("cliente02")
                .orElseThrow();

        assertThat(actualizado.getNombres()).isEqualTo("Cliente Actualizado");
    }

    @Test
    void debeEliminarUsuario() {
        Usuario usuario = Usuario.builder()
                .nomusuario("cliente03")
                .email("cliente03@sigip.test")
                .password("hash")
                .nombres("Cliente")
                .apellidos("Tres")
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.saveAndFlush(usuario);
        usuarioRepository.deleteById(guardado.getIdusuario());
        usuarioRepository.flush();

        assertThat(usuarioRepository.findById(guardado.getIdusuario())).isEmpty();
    }
}
