package pe.edu.cibertec.sigip.reportservice.client;

import java.math.BigDecimal;

/** Insumo con stock igual o menor al mínimo (inventory-service). */
public record InsumoCritico(Integer idinsumo, String codigo, String nombre, String unidadMedida,
                            BigDecimal stockActual, BigDecimal stockMinimo) {
}
