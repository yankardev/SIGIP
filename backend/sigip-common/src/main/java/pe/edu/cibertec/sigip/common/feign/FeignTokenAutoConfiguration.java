package pe.edu.cibertec.sigip.common.feign;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Propaga el token JWT del usuario en las llamadas Feign entre microservicios.
 * Así el servicio destino también puede validar identidad y permisos (seguridad de extremo a extremo).
 */
@AutoConfiguration
@ConditionalOnClass(RequestInterceptor.class)
public class FeignTokenAutoConfiguration {

    @Bean
    public RequestInterceptor propagarTokenInterceptor() {
        return template -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
                String authorization = attrs.getRequest().getHeader("Authorization");
                if (authorization != null) {
                    template.header("Authorization", authorization);
                }
            }
        };
    }
}
