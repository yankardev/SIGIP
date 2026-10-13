package pe.edu.cibertec.sigip.inventoryservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.sigip.inventoryservice.model.Insumo;
public interface InsumoRepository extends JpaRepository<Insumo,Integer>{}