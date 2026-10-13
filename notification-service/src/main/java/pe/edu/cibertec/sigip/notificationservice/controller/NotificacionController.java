package pe.edu.cibertec.sigip.notificationservice.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.notificationservice.model.Notificacion;
import pe.edu.cibertec.sigip.notificationservice.service.NotificacionService;
import java.util.List;
@RestController @RequestMapping("/api/v1/notificaciones") @RequiredArgsConstructor
public class NotificacionController{
 private final NotificacionService service;
 @GetMapping public ResponseEntity<List<Notificacion>> listar(){return ResponseEntity.ok(service.listar());}
 @GetMapping("/{id}") public ResponseEntity<Notificacion> obtener(@PathVariable Integer id){return ResponseEntity.ok(service.obtener(id));}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Notificacion> guardar(@RequestBody Notificacion x){return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(x));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Notificacion> actualizar(@PathVariable Integer id,@RequestBody Notificacion x){return ResponseEntity.ok(service.actualizar(id,x));}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Void> eliminar(@PathVariable Integer id){service.eliminar(id);return ResponseEntity.noContent().build();}
}