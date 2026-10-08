package pe.edu.cibertec.sigip.productionservice.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.productionservice.model.OrdenProduccion;
import pe.edu.cibertec.sigip.productionservice.service.OrdenProduccionService;
import java.util.List;
@RestController @RequestMapping("/api/v1/ordenes-produccion") @RequiredArgsConstructor
public class OrdenProduccionController{
 private final OrdenProduccionService service;
 @GetMapping public ResponseEntity<List<OrdenProduccion>> listar(){return ResponseEntity.ok(service.listar());}
 @GetMapping("/{id}") public ResponseEntity<OrdenProduccion> obtener(@PathVariable Integer id){return ResponseEntity.ok(service.obtener(id));}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<OrdenProduccion> guardar(@RequestBody OrdenProduccion x){return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(x));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<OrdenProduccion> actualizar(@PathVariable Integer id,@RequestBody OrdenProduccion x){return ResponseEntity.ok(service.actualizar(id,x));}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> eliminar(@PathVariable Integer id){service.eliminar(id);return ResponseEntity.noContent().build();}
}