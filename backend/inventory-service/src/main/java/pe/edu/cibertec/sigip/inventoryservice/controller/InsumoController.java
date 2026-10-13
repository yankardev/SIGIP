package pe.edu.cibertec.sigip.inventoryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.inventoryservice.dto.*;
import pe.edu.cibertec.sigip.inventoryservice.service.InsumoService;

import java.util.List;

/** Inventario de insumos (uso interno: rol ADMIN). */
@RestController
@RequestMapping("/api/v1/insumos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InsumoController {

    private final InsumoService service;

    @GetMapping
    public ResponseEntity<List<InsumoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<InsumoResponse>> bajoStock() {
        return ResponseEntity.ok(service.bajoStock());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<InsumoResponse> registrar(@Valid @RequestBody InsumoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody InsumoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/movimientos")
    public ResponseEntity<MovimientoResponse> movimiento(@PathVariable Integer id,
                                                         @Valid @RequestBody MovimientoRequest request,
                                                         Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrarMovimiento(id, request, auth.getName()));
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoResponse>> kardex(@PathVariable Integer id) {
        return ResponseEntity.ok(service.kardex(id));
    }
}
