package pe.edu.cibertec.sigip.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;

import java.util.List;
import java.util.Optional;

public interface ServicioImpresionRepository extends JpaRepository<ServicioImpresion, Integer> {

    /**
     * OPTIMIZACIÓN (problema N+1): trae servicio y categoría en UNA sola consulta con JOIN FETCH.
     * Con findAll() la categoría (LAZY) se cargaba con una consulta extra por cada servicio.
     */
    @Query("select s from ServicioImpresion s join fetch s.categoria order by s.nombre")
    List<ServicioImpresion> listarConCategoria();

    @Query("select s from ServicioImpresion s join fetch s.categoria where s.activo = true order by s.nombre")
    List<ServicioImpresion> listarActivosConCategoria();

    @Query("select s from ServicioImpresion s join fetch s.categoria where s.idservicio = :id")
    Optional<ServicioImpresion> buscarConCategoria(Integer id);

    List<ServicioImpresion> findByActivoTrueOrderByNombreAsc();

    boolean existsByCategoriaIdcategoria(Integer idcategoria);
}
