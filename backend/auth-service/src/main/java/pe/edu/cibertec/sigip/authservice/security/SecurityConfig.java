package pe.edu.cibertec.sigip.authservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.cibertec.sigip.common.error.RespuestasSeguridad;
import pe.edu.cibertec.sigip.common.security.FiltroJwtAuth;

/**
 * auth-service es el único servicio que autentica con usuario y contraseña,
 * por eso define su propia cadena de seguridad (las demás usan la de sigip-common).
 * El AuthenticationManager se arma automáticamente con DetalleUsuarioService + BCrypt.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   FiltroJwtAuth filtroJwtAuth) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(RespuestasSeguridad::noAutenticado)
                        .accessDeniedHandler(RespuestasSeguridad::accesoDenegado))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/registro").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info",
                                "/actuator/prometheus", "/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(filtroJwtAuth, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
