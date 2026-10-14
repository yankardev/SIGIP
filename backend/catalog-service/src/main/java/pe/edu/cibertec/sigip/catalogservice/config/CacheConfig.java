package pe.edu.cibertec.sigip.catalogservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Activa la caché de Spring. El proveedor (Caffeine) y su política de expiración
 * se configuran en application.properties (spring.cache.caffeine.spec).
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
