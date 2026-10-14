package pe.edu.cibertec.sigip.inventoryservice.dto;

import pe.edu.cibertec.sigip.inventoryservice.model.Insumo;

import java.math.BigDecimal;

public record InsumoResponse(
        Integer idinsumo,
        String codigo,
        String nombre,
        String unidadMedida,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        Boolean activo,
        boolean bajoStock
) {
    public static InsumoResponse from(Insumo i) {
        return new InsumoResponse(i.getIdinsumo(), i.getCodigo(), i.getNombre(), i.getUnidadMedida(),
                i.getStockActual(), i.getStockMinimo(), i.getActivo(), i.bajoStock());
    }
}
