package pe.edu.cibertec.sigip.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.edu.cibertec.sigip.authservice.model.Rol;
import pe.edu.cibertec.sigip.authservice.model.Usuario;
import pe.edu.cibertec.sigip.authservice.repository.RolRepository;
import pe.edu.cibertec.sigip.authservice.repository.UsuarioRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${sigip.admin.username:admin}")
    private String adminUsername;

    @Value("${sigip.admin.email:admin@sigip.local}")
    private String adminEmail;

    @Value("${sigip.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Rol cliente = crearRolSiNoExiste("CLIENTE");
        Rol admin = crearRolSiNoExiste("ADMIN");

        if (!adminPassword.isBlank() && !usuarioRepository.existsByNomusuario(adminUsername)) {
            Usuario usuario = Usuario.builder()
                    .nomusuario(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .nombres("Administrador")
                    .apellidos("SIGIP")
                    .activo(true)
                    .roles(Set.of(admin))
                    .build();

            usuarioRepository.save(usuario);
        }
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNomrol(nombre)
                .orElseGet(() -> rolRepository.save(
                        Rol.builder().nomrol(nombre).build()
                ));
    }
}
