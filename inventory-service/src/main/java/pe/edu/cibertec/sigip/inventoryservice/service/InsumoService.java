package pe.edu.cibertec.sigip.inventoryservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.cibertec.sigip.inventoryservice.model.Insumo;
import pe.edu.cibertec.sigip.inventoryservice.repository.InsumoRepository;
import java.util.List;
@Service @RequiredArgsConstructor
public class InsumoService{
 private final InsumoRepository repo;
 public List<Insumo> listar(){return repo.findAll();}
 public Insumo obtener(Integer id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Insumo no encontrado"));}
 public Insumo guardar(Insumo x){return repo.save(x);}
 public Insumo actualizar(Integer id,Insumo x){Insumo a=obtener(id);x.setIdinsumo(a.getIdinsumo());return repo.save(x);}
 public void eliminar(Integer id){repo.deleteById(obtener(id).getIdinsumo());}
}