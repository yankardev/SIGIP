package pe.edu.cibertec.sigip.productionservice.model;

import java.util.EnumSet;
import java.util.Set;

/** Estados de la orden de producción y transiciones permitidas. */
public enum EstadoOrden {
    PENDIENTE,
    EN_PROCESO,
    /** No pasó el control de calidad: requiere reproceso. */
    OBSERVADA,
    TERMINADA;

    public Set<EstadoOrden> siguientes() {
        return switch (this) {
            case PENDIENTE, OBSERVADA -> EnumSet.of(EN_PROCESO);
            case EN_PROCESO -> EnumSet.of(TERMINADA, OBSERVADA);
            case TERMINADA -> EnumSet.noneOf(EstadoOrden.class);
        };
    }
}
