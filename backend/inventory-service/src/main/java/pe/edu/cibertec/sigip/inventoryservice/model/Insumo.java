package pe.edu.cibertec.sigip.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Material usado en producción (papel, tinta, vinil...). */
@Entity
@Table(name = "insumos", uniqueConstraints = @UniqueConstraint(name = "uk_insumo_codigo", columnNames = "codigo"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idinsumo;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 30)
    private String unidadMedida;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockActual;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockMinimo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Bloqueo optimista: si dos salidas simultáneas leen el mismo stock, solo una se confirma
     * y la otra recibe 409 (evita sobregirar el inventario).
     */
    @Version
    private Long version;

    public boolean bajoStock() {
        return stockActual.compareTo(stockMinimo) <= 0;
    }
}
