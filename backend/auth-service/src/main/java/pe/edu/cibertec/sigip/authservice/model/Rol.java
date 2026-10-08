package pe.edu.cibertec.sigip.authservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idrol;

    @Column(name = "nomrol", nullable = false, unique = true, length = 50)
    private String nomrol;
}
