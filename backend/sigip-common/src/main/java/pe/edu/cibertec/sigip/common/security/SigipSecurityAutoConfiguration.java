package pe.edu.cibertec.sigip.common.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.cibertec.sigip.common.error.GlobalExceptionHandler;
import pe.edu.cibertec.sigip.common.error.RespuestasSeguridad;

/**
 * Configuración de seguridad compartida por todos los microservicios con API REST.
 * <ul>
 *   <li>API sin estado (STATELESS) protegida con JWT.</li>
 *   <li>Rutas públicas configurables por propiedad (sin repetir código en cada servicio).</li>
 *   <li>Respuestas 401/403 en JSON con el mismo formato que el resto de errores.</li>
 *   <li>@PreAuthorize habilitado para controlar permisos por rol.</li>
 * </ul>
 * Un servicio puede definir su propio {@link SecurityFilterChain} (como auth-service) y esta
 * configuración por defecto se omite.
 */
@AutoConfiguration(before = ServletWebSecurityAutoConfiguration.class,
        beforeName = {
                "org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration",
                "org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration"})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SigipSecurityProperties.class)
@EnableMethodSecurity
@Import(GlobalExceptionHandler.class)
public class SigipSecurityAutoConfiguration {

    static final String[] RUTAS_MONITOREO = {
            "/actuator/health", "/actuator/health/**", "/actuator/info", "/actuator/prometheus", "/error"
    };

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService(SigipSecurityProperties properties) {
        return new JwtService(properties.getJwt().getSecret(), properties.getJwt().getExpirationMinutes());
    }

    @Bean
    @ConditionalOnMissingBean
    public FiltroJwtAuth filtroJwtAuth(JwtService jwtService) {
        return new FiltroJwtAuth(jwtService);
    }

    /**
     * Los servicios de negocio no autentican usuarios (solo validan el JWT). Este bean evita que
     * Spring Boot cree un usuario en memoria con una contraseña generada que se imprime en el log.
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService sinUsuariosLocales() {
        return username -> {
            throw new UsernameNotFoundException("Este servicio no autentica usuarios; use auth-service");
        };
    }

    /** Evita que el filtro JWT se registre dos veces (en el contenedor y en la cadena de seguridad). */
    @Bean
    public FilterRegistrationBean<FiltroJwtAuth> filtroJwtAuthRegistration(FiltroJwtAuth filtro) {
        FilterRegistrationBean<FiltroJwtAuth> registration = new FilterRegistrationBean<>(filtro);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain sigipSecurityFilterChain(HttpSecurity http,
                                                        FiltroJwtAuth filtroJwtAuth,
                                                        SigipSecurityProperties properties) throws Exception {
        String[] publicas = properties.getSecurity().getPublicPaths().toArray(String[]::new);
        String[] publicasGet = properties.getSecurity().getPublicGetPaths().toArray(String[]::new);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(RespuestasSeguridad::noAutenticado)
                        .accessDeniedHandler(RespuestasSeguridad::accesoDenegado))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(RUTAS_MONITOREO).permitAll();
                    if (publicas.length > 0) {
                        auth.requestMatchers(publicas).permitAll();
                    }
                    if (publicasGet.length > 0) {
                        auth.requestMatchers(HttpMethod.GET, publicasGet).permitAll();
                    }
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(filtroJwtAuth, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
