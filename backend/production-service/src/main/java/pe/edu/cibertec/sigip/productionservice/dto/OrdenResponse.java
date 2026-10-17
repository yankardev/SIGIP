package pe.edu.cibertec.sigip.productionservice.dto;

import pe.edu.cibertec.sigip.productionservice.model.EstadoOrden;
import pe.edu.cibertec.sigip.productionservice.model.OrdenProduccion;
import pe.edu.cibertec.sigip.productionservice.model.Prioridad;

import java.time.LocalDateTime;

public record OrdenResponse(
        Integer idorden,
        Integer idpedido,
        String descripcionTrabajo,
        EstadoOrden estado,
        Prioridad prioridad,
        String responsable,
        Integer reprocesos,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String observacion
) {
    public static OrdenResponse from(OrdenProduccion o) {
        return new OrdenResponse(o.getIdorden(), o.getIdpedido(), o.getDescripcionTrabajo(), o.getEstado(),
                o.getPrioridad(), o.getResponsable(), o.getReprocesos(), o.getFechaRegistro(),
                o.getFechaInicio(), o.getFechaFin(), o.getObservacion());
    }
}
