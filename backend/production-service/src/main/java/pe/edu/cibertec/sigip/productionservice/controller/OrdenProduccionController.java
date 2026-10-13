package pe.edu.cibertec.sigip.productionservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.sigip.productionservice.dto.OrdenRequest;
import pe.edu.cibertec.sigip.productionservice.dto.OrdenResponse;
import pe.edu.cibertec.sigip.productionservice.dto.ResultadoCalidadRequest;
import pe.edu.cibertec.sigip.productionservice.model.EstadoOrden;
import pe.edu.cibertec.sigip.productionservice.service.OrdenProduccionService;

import java.util.List;

/** Gestión de órdenes de producción (personal interno: rol ADMIN). */
@RestController
@RequestMapping("/api/v1/ordenes-produccion")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrdenProduccionController {

    private final OrdenProduccionService service;

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listar(@RequestParam(required = false) EstadoOrden estado) {
        return ResponseEntity.ok(service.listar(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> registrar(@Valid @RequestBody OrdenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody OrdenRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/inicio")
    public ResponseEntity<OrdenResponse> iniciar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.iniciar(id));
    }

    @PostMapping("/{id}/calidad")
    public ResponseEntity<OrdenResponse> calidad(@PathVariable Integer id,
                                                 @Valid @RequestBody ResultadoCalidadRequest request) {
        return ResponseEntity.ok(service.registrarCalidad(id, request));
    }
}
