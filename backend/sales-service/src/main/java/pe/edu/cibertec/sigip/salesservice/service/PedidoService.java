package pe.edu.cibertec.sigip.salesservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.cibertec.sigip.salesservice.model.Pedido;
import pe.edu.cibertec.sigip.salesservice.repository.PedidoRepository;
import java.util.List;
@Service @RequiredArgsConstructor
public class PedidoService{
 private final PedidoRepository repo;
 public List<Pedido> listar(){return repo.findAll();}
 public Pedido obtener(Integer id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido no encontrado"));}
 public Pedido guardar(Pedido x){return repo.save(x);}
 public Pedido actualizar(Integer id,Pedido x){Pedido a=obtener(id);x.setIdpedido(a.getIdpedido());return repo.save(x);}
 public void eliminar(Integer id){repo.deleteById(obtener(id).getIdpedido());}
}