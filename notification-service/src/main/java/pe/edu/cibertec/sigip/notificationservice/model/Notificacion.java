package pe.edu.cibertec.sigip.notificationservice.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="notificaciones") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Notificacion{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Integer idnotificacion;

 @Column(nullable=false,length=150) private String destinatario;
 @Column(nullable=false,length=30) private String canal;
 @Column(nullable=false,length=200) private String asunto;
 @Column(nullable=false,length=1000) private String mensaje;
 @Column(nullable=false,length=30) private String estado;
 @Column(nullable=false) private java.time.LocalDateTime fecha;
}