package pe.edu.cibertec.sigip.inventoryservice.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="insumos") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Insumo{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer idinsumo;

 @Column(nullable=false,length=150) private String nombre;
 @Column(nullable=false,length=30) private String unidadMedida;
 @Column(nullable=false,precision=12,scale=3) private java.math.BigDecimal stockActual;
 @Column(nullable=false,precision=12,scale=3) private java.math.BigDecimal stockMinimo;
 @Column(nullable=false) private Boolean activo;
}