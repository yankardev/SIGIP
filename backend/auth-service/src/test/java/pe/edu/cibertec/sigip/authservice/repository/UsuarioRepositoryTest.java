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
    void debeInsertarUsuario() {
        Usuario usuario = usuarioRepository.save(usuario("cliente01"));

        assertThat(usuario.getIdusuario()).isNotNull();
        assertThat(usuario.getNomusuario()).isEqualTo("cliente01");
    }

    @Test
    void debeListarUsuarios() {
        usuarioRepository.save(usuario("cliente01"));
        usuarioRepository.save(usuario("cliente02"));

        assertThat(usuarioRepository.findAll()).hasSize(2);
    }

    @Test
    void debeActualizarUsuario() {
        Usuario guardado = usuarioRepository.saveAndFlush(usuario("cliente03"));

        guardado.setNombres("Cliente Actualizado");
        usuarioRepository.saveAndFlush(guardado);

        Usuario actualizado = usuarioRepository
                .findByNomusuario("cliente03")
                .orElseThrow();

        assertThat(actualizado.getNombres()).isEqualTo("Cliente Actualizado");
    }

    @Test
    void debeEliminarUsuario() {
        Usuario guardado = usuarioRepository.saveAndFlush(usuario("cliente04"));

        usuarioRepository.deleteById(guardado.getIdusuario());
        usuarioRepository.flush();

        assertThat(usuarioRepository.findById(guardado.getIdusuario())).isEmpty();
    }

    private Usuario usuario(String username) {
        return Usuario.builder()
                .nomusuario(username)
                .email(username + "@sigip.test")
                .password("hash")
                .nombres("Cliente")
                .apellidos("Prueba")
                .activo(true)
                .build();
    }
}
