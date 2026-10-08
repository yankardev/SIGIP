package pe.edu.cibertec.sigip.salesservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.sigip.salesservice.model.Pedido;
public interface PedidoRepository extends JpaRepository<Pedido,Integer>{}