package pe.edu.cibertec.sigip.authservice.repository;

import pe.edu.cibertec.sigip.authservice.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByNomusuario(String nomusuario);

    boolean existsByNomusuario(String nomusuario);

    boolean existsByEmail(String email);
}
