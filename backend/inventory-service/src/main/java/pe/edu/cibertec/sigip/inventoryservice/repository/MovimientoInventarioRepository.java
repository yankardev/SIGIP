package pe.edu.cibertec.sigip.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.sigip.inventoryservice.model.MovimientoInventario;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByInsumoIdinsumoOrderByFechaDesc(Integer idinsumo);

    boolean existsByInsumoIdinsumo(Integer idinsumo);
}
