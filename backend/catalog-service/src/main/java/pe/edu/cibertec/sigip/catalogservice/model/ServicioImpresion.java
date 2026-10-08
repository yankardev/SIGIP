package pe.edu.cibertec.sigip.catalogservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servicio_impresion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioImpresion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idservicio;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereCotizacion = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcategoria", nullable = false)
    private Categoria categoria;
}
