package pe.edu.cibertec.sigip.productionservice.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="ordenes_produccion") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class OrdenProduccion{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer idorden;

 @Column(nullable=false) private Integer idpedido;
 @Column(nullable=false,length=30) private String estado;
 @Column(nullable=false,length=20) private String prioridad;
 private java.time.LocalDateTime fechaInicio;
 private java.time.LocalDateTime fechaFin;
 @Column(length=500) private String observacion;
}