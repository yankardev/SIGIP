package pe.edu.cibertec.sigip.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.cibertec.sigip.inventoryservice.model.Insumo;

import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    List<Insumo> findAllByOrderByNombreAsc();

    boolean existsByCodigoIgnoreCase(String codigo);

    /** Filtro hecho en la BD (no se traen todos los insumos para filtrarlos en Java). */
    @Query("select i from Insumo i where i.activo = true and i.stockActual <= i.stockMinimo order by i.nombre")
    List<Insumo> bajoStock();
}
