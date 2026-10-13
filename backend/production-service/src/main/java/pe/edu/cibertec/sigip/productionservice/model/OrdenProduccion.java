package pe.edu.cibertec.sigip.productionservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** Orden de producción: una por pedido (restricción única). */
@Entity
@Table(name = "ordenes_produccion",
        uniqueConstraints = @UniqueConstraint(name = "uk_orden_pedido", columnNames = "idpedido"),
        indexes = @Index(name = "idx_orden_estado", columnList = "estado"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idorden;

    @Column(nullable = false)
    private Integer idpedido;

    @Column(length = 150)
    private String descripcionTrabajo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridad prioridad;

    @Column(length = 100)
    private String responsable;

    /** Veces que la orden volvió a proceso por observaciones de calidad. */
    @Column(nullable = false)
    @Builder.Default
    private Integer reprocesos = 0;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @Column(length = 500)
    private String observacion;

    @Version
    private Long version;
}
