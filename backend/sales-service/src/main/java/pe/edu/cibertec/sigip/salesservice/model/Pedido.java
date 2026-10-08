package pe.edu.cibertec.sigip.salesservice.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="pedidos") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Pedido{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer idpedido;

 @Column(nullable=false) private Integer idcliente;
 @Column(nullable=false) private Integer idservicio;
 @Column(nullable=false) private Integer cantidad;
 @Column(nullable=false,precision=12,scale=2) private java.math.BigDecimal total;
 @Column(nullable=false,length=30) private String estado;
 @Column(length=500) private String observacion;
 @Column(nullable=false) private java.time.LocalDateTime fechaPedido;
}