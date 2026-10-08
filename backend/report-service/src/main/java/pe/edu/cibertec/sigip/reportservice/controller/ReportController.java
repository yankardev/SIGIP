package pe.edu.cibertec.sigip.reportservice.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
@RestController @RequestMapping("/api/v1/reportes")
public class ReportController{
 @GetMapping("/estado") @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<Map<String,Object>> estado(){return ResponseEntity.ok(Map.of("servicio","report-service","estado","OPERATIVO","fecha",LocalDateTime.now()));}
}