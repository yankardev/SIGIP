package pe.edu.cibertec.sigip.designservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.cibertec.sigip.designservice.model.Diseno;
import pe.edu.cibertec.sigip.designservice.repository.DisenoRepository;
import java.util.List;
@Service @RequiredArgsConstructor
public class DisenoService{
 private final DisenoRepository repo;
 public List<Diseno> listar(){return repo.findAll();}
 public Diseno obtener(Integer id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Diseno no encontrado"));}
 public Diseno guardar(Diseno x){return repo.save(x);}
 public Diseno actualizar(Integer id,Diseno x){Diseno a=obtener(id);x.setIddiseno(a.getIddiseno());return repo.save(x);}
 public void eliminar(Integer id){repo.deleteById(obtener(id).getIddiseno());}
}