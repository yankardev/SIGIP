package pe.edu.cibertec.sigip.reportservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.sigip.common.error.ServicioNoDisponibleException;
import pe.edu.cibertec.sigip.reportservice.client.InsumoCritico;
import pe.edu.cibertec.sigip.reportservice.client.InventoryServiceClient;
import pe.edu.cibertec.sigip.reportservice.client.ResumenVentas;
import pe.edu.cibertec.sigip.reportservice.client.SalesServiceClient;
import pe.edu.cibertec.sigip.reportservice.dto.DashboardResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final SalesServiceClient sales;
    private final InventoryServiceClient inventory;

    public ResumenVentas ventas() {
        return sales.resumen();
    }

    public List<InsumoCritico> inventarioCritico() {
        return inventory.bajoStock();
    }

    public DashboardResponse dashboard() {
        List<String> avisos = new ArrayList<>();

        ResumenVentas ventas = null;
        try {
            ventas = sales.resumen();
        } catch (ServicioNoDisponibleException e) {
            avisos.add(e.getMessage());
        }

        List<InsumoCritico> criticos = List.of();
        try {
            criticos = inventory.bajoStock();
        } catch (ServicioNoDisponibleException e) {
            avisos.add(e.getMessage());
        }

        Map<String, Long> porEstado = ventas != null ? ventas.pedidosPorEstado() : Map.of();
        long porAtender = suma(porEstado, "REGISTRADO", "PAGO_EN_REVISION", "CONFIRMADO");
        long enTaller = suma(porEstado, "EN_DISENO", "EN_PRODUCCION");
        if (!criticos.isEmpty()) {
            avisos.add(criticos.size() + " insumo(s) con stock bajo el mínimo");
        }
        return new DashboardResponse(LocalDateTime.now(), ventas, porAtender, enTaller, criticos, avisos);
    }

    private static long suma(Map<String, Long> porEstado, String... estados) {
        long total = 0;
        for (String e : estados) {
            total += porEstado.getOrDefault(e, 0L);
        }
        return total;
    }
}
