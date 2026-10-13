package pe.edu.cibertec.sigip.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import pe.edu.cibertec.sigip.catalogservice.dto.ServicioImpresionRequest;
import pe.edu.cibertec.sigip.catalogservice.dto.ServicioImpresionResponse;
import pe.edu.cibertec.sigip.catalogservice.service.ServicioImpresionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
public class ServicioImpresionController {

    private final ServicioImpresionService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioImpresionResponse>> listar() {
        return ResponseEntity.ok(servicioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioImpresionResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(servicioService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicioImpresionResponse> registrar(
            @Valid @RequestBody ServicioImpresionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicioService.registrar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicioImpresionResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ServicioImpresionRequest request
    ) {
        return ResponseEntity.ok(servicioService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
