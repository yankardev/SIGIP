package pe.edu.cibertec.sigip.reportservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.cibertec.sigip.reportservice.client.InsumoCritico;
import pe.edu.cibertec.sigip.reportservice.client.ResumenVentas;
import pe.edu.cibertec.sigip.reportservice.dto.DashboardResponse;
import pe.edu.cibertec.sigip.reportservice.service.ReporteService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReporteService service;

    @GetMapping("/estado")
    public Map<String, Object> estado() {
        return Map.of("servicio", "report-service", "estado", "OPERATIVO", "fecha", LocalDateTime.now());
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }

    @GetMapping("/ventas")
    public ResumenVentas ventas() {
        return service.ventas();
    }

    @GetMapping("/inventario-critico")
    public List<InsumoCritico> inventarioCritico() {
        return service.inventarioCritico();
    }
}
