package pe.edu.cibertec.sigip.catalogservice.dto;

import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;

import java.math.BigDecimal;

public record ServicioImpresionResponse(
        Integer idservicio,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        Boolean requiereCotizacion,
        Boolean activo,
        Integer idcategoria,
        String categoria
) {

    public static ServicioImpresionResponse from(ServicioImpresion servicio) {
        return new ServicioImpresionResponse(
                servicio.getIdservicio(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getPrecioBase(),
                servicio.getRequiereCotizacion(),
                servicio.getActivo(),
                servicio.getCategoria().getIdcategoria(),
                servicio.getCategoria().getNombre()
        );
    }
}
