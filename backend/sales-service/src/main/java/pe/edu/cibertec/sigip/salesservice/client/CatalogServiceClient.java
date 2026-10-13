package pe.edu.cibertec.sigip.salesservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="catalog-service")
public interface CatalogServiceClient{@GetMapping("/api/v1/servicios/{id}") ServicioCatalogoResponse obtenerServicio(@PathVariable("id")Integer id);}