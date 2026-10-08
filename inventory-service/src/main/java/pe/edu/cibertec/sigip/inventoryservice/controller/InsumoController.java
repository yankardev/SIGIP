package pe.edu.cibertec.sigip.inventoryservice.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.inventoryservice.model.Insumo;
import pe.edu.cibertec.sigip.inventoryservice.service.InsumoService;
import java.util.List;
@RestController @RequestMapping("/api/v1/insumos") @RequiredArgsConstructor
public class InsumoController{
 private final InsumoService service;
 @GetMapping public ResponseEntity<List<Insumo>> listar(){return ResponseEntity.ok(service.listar());}
 @GetMapping("/{id}") public ResponseEntity<Insumo> obtener(@PathVariable Integer id){return ResponseEntity.ok(service.obtener(id));}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Insumo> guardar(@RequestBody Insumo x){return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(x));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Insumo> actualizar(@PathVariable Integer id,@RequestBody Insumo x){return ResponseEntity.ok(service.actualizar(id,x));}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> eliminar(@PathVariable Integer id){service.eliminar(id);return ResponseEntity.noContent().build();}
}