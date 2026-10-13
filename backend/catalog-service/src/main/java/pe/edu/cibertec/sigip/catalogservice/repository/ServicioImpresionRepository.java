package pe.edu.cibertec.sigip.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;

import java.util.List;

public interface ServicioImpresionRepository extends JpaRepository<ServicioImpresion, Integer> {

    List<ServicioImpresion> findByActivoTrueOrderByNombreAsc();
}
