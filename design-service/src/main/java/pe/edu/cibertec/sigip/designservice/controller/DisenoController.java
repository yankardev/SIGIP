package pe.edu.cibertec.sigip.designservice.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.designservice.model.Diseno;
import pe.edu.cibertec.sigip.designservice.service.DisenoService;
import java.util.List;
@RestController @RequestMapping("/api/v1/disenos") @RequiredArgsConstructor
public class DisenoController{
 private final DisenoService service;
 @GetMapping public ResponseEntity<List<Diseno>> listar(){return ResponseEntity.ok(service.listar());}
 @GetMapping("/{id}") public ResponseEntity<Diseno> obtener(@PathVariable Integer id){return ResponseEntity.ok(service.obtener(id));}
 @PostMapping @PreAuthorize("hasRole('CLIENTE')") public ResponseEntity<Diseno> guardar(@RequestBody Diseno x){return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(x));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('CLIENTE')") public ResponseEntity<Diseno> actualizar(@PathVariable Integer id,@RequestBody Diseno x){return ResponseEntity.ok(service.actualizar(id,x));}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('CLIENTE')") public ResponseEntity<Void> eliminar(@PathVariable Integer id){service.eliminar(id);return ResponseEntity.noContent().build();}
}