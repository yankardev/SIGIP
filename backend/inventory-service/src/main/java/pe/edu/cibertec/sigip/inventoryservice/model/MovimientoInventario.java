package pe.edu.cibertec.sigip.inventoryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Kárdex: cada cambio de stock queda registrado con quién, cuándo y por qué. */
@Entity
@Table(name = "movimientos_inventario", indexes = @Index(name = "idx_mov_insumo", columnList = "idinsumo"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmovimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idinsumo", nullable = false)
    private Insumo insumo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimiento tipo;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal stockResultante;

    @Column(nullable = false, length = 200)
    private String motivo;

    /** Pedido relacionado (opcional), para trazar el consumo real por trabajo. */
    private Integer idpedido;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
