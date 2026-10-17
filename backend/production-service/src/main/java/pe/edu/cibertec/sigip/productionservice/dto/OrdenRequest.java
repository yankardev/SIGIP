package pe.edu.cibertec.sigip.productionservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.cibertec.sigip.productionservice.model.Prioridad;

public record OrdenRequest(
        @NotNull(message = "El pedido es obligatorio")
        Integer idpedido,

        @NotNull(message = "La prioridad es obligatoria (BAJA, MEDIA o ALTA)")
        Prioridad prioridad,

        @Size(max = 100, message = "El responsable no puede superar 100 caracteres")
        String responsable,

        @Size(max = 500, message = "La observación no puede superar 500 caracteres")
        String observacion
) {
}
