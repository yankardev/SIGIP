package pe.edu.cibertec.sigip.notificationservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.cibertec.sigip.notificationservice.model.Notificacion;
import pe.edu.cibertec.sigip.notificationservice.repository.NotificacionRepository;
import java.util.List;
@Service @RequiredArgsConstructor
public class NotificacionService{
 private final NotificacionRepository repo;
 public List<Notificacion> listar(){return repo.findAll();}
 public Notificacion obtener(Integer id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notificacion no encontrado"));}
 public Notificacion guardar(Notificacion x){return repo.save(x);}
 public Notificacion actualizar(Integer id,Notificacion x){Notificacion a=obtener(id);x.setIdnotificacion(a.getIdnotificacion());return repo.save(x);}
 public void eliminar(Integer id){repo.deleteById(obtener(id).getIdnotificacion());}
}