package pe.edu.cibertec.sigip.salesservice.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.salesservice.model.Pedido;
import pe.edu.cibertec.sigip.salesservice.service.PedidoService;
import java.util.List;
@RestController @RequestMapping("/api/v1/pedidos") @RequiredArgsConstructor
public class PedidoController{
 private final PedidoService service;
 @GetMapping public ResponseEntity<List<Pedido>> listar(){return ResponseEntity.ok(service.listar());}
 @GetMapping("/{id}") public ResponseEntity<Pedido> obtener(@PathVariable Integer id){return ResponseEntity.ok(service.obtener(id));}
 @PostMapping @PreAuthorize("hasRole('CLIENTE')") public ResponseEntity<Pedido> guardar(@RequestBody Pedido x){return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(x));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('CLIENTE')") public ResponseEntity<Pedido> actualizar(@PathVariable Integer id,@RequestBody Pedido x){return ResponseEntity.ok(service.actualizar(id,x));}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('CLIENTE')") public ResponseEntity<Void> eliminar(@PathVariable Integer id){service.eliminar(id);return ResponseEntity.noContent().build();}
}