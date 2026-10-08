package pe.edu.cibertec.sigip.notificationservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.sigip.notificationservice.model.Notificacion;
public interface NotificacionRepository extends JpaRepository<Notificacion,Integer>{}