package pe.edu.cibertec.sigip.authservice.repository;

import pe.edu.cibertec.sigip.authservice.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNomrol(String nomrol);
}
