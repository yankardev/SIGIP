package pe.edu.cibertec.sigip.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);
}
