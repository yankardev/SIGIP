package pe.edu.cibertec.sigip.productionservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.cibertec.sigip.productionservice.model.OrdenProduccion;
import pe.edu.cibertec.sigip.productionservice.repository.OrdenProduccionRepository;
import java.util.List;
@Service @RequiredArgsConstructor
public class OrdenProduccionService{
 private final OrdenProduccionRepository repo;
 public List<OrdenProduccion> listar(){return repo.findAll();}
 public OrdenProduccion obtener(Integer id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"OrdenProduccion no encontrado"));}
 public OrdenProduccion guardar(OrdenProduccion x){return repo.save(x);}
 public OrdenProduccion actualizar(Integer id,OrdenProduccion x){OrdenProduccion a=obtener(id);x.setIdorden(a.getIdorden());return repo.save(x);}
 public void eliminar(Integer id){repo.deleteById(obtener(id).getIdorden());}
}