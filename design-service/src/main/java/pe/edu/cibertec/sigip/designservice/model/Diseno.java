package pe.edu.cibertec.sigip.designservice.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="disenos") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Diseno{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer iddiseno;

 @Column(nullable=false) private Integer idpedido;
 @Column(nullable=false,length=200) private String nombreArchivo;
 @Column(nullable=false) private Integer version;
 @Column(nullable=false,length=30) private String estado;
 @Column(nullable=false) private Boolean aprobado;
 @Column(length=500) private String observacion;
 @Column(nullable=false) private java.time.LocalDateTime fechaRegistro;
}