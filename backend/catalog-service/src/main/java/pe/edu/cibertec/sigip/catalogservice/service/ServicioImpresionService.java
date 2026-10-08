package pe.edu.cibertec.sigip.catalogservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.catalogservice.dto.ServicioImpresionRequest;
import pe.edu.cibertec.sigip.catalogservice.dto.ServicioImpresionResponse;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;
import pe.edu.cibertec.sigip.catalogservice.repository.CategoriaRepository;
import pe.edu.cibertec.sigip.catalogservice.repository.ServicioImpresionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioImpresionService {

    private final ServicioImpresionRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ServicioImpresionResponse> listar() {
        return servicioRepository.findAll().stream()
                .map(ServicioImpresionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicioImpresionResponse listarActivos() {
        return null;
    }

    @Transactional(readOnly = true)
    public ServicioImpresionResponse obtener(Integer id) {
        return ServicioImpresionResponse.from(
                servicioRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Servicio de impresión no encontrado"))
        );
    }

    @Transactional
    public ServicioImpresionResponse registrar(ServicioImpresionRequest request) {
        Categoria categoria = categoriaRepository.findById(request.idcategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        ServicioImpresion servicio = ServicioImpresion.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precioBase(request.precioBase())
                .requiereCotizacion(request.requiereCotizacion())
                .activo(true)
                .categoria(categoria)
                .build();

        return ServicioImpresionResponse.from(servicioRepository.save(servicio));
    }

    @Transactional
    public ServicioImpresionResponse actualizar(Integer id, ServicioImpresionRequest request) {
        ServicioImpresion servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio de impresión no encontrado"));

        Categoria categoria = categoriaRepository.findById(request.idcategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        servicio.setNombre(request.nombre());
        servicio.setDescripcion(request.descripcion());
        servicio.setPrecioBase(request.precioBase());
        servicio.setRequiereCotizacion(request.requiereCotizacion());
        servicio.setCategoria(categoria);

        return ServicioImpresionResponse.from(servicioRepository.save(servicio));
    }

    @Transactional
    public void eliminar(Integer id) {
        ServicioImpresion servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio de impresión no encontrado"));

        servicioRepository.delete(servicio);
    }
}
