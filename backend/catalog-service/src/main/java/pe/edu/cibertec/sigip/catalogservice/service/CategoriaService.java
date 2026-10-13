package pe.edu.cibertec.sigip.catalogservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.catalogservice.config.CacheNombres;
import pe.edu.cibertec.sigip.catalogservice.dto.CategoriaRequest;
import pe.edu.cibertec.sigip.catalogservice.dto.CategoriaResponse;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.repository.CategoriaRepository;
import pe.edu.cibertec.sigip.catalogservice.repository.ServicioImpresionRepository;
import pe.edu.cibertec.sigip.common.error.RecursoNoEncontradoException;
import pe.edu.cibertec.sigip.common.error.ReglaNegocioException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ServicioImpresionRepository servicioRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNombres.CATEGORIAS, key = "'todas'")
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Integer id) {
        return CategoriaResponse.from(buscar(id));
    }

    @Transactional
    @CacheEvict(value = CacheNombres.CATEGORIAS, allEntries = true)
    public CategoriaResponse registrar(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = Categoria.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .activo(true)
                .build();

        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    /** El nombre de la categoría se muestra en cada servicio, por eso también se limpia la caché de servicios. */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNombres.CATEGORIAS, allEntries = true),
            @CacheEvict(value = {CacheNombres.SERVICIOS, CacheNombres.SERVICIO}, allEntries = true)
    })
    public CategoriaResponse actualizar(Integer id, CategoriaRequest request) {
        Categoria categoria = buscar(id);

        if (!categoria.getNombre().equalsIgnoreCase(request.nombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ReglaNegocioException("Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional
    @CacheEvict(value = CacheNombres.CATEGORIAS, allEntries = true)
    public void eliminar(Integer id) {
        Categoria categoria = buscar(id);
        if (servicioRepository.existsByCategoriaIdcategoria(id)) {
            throw new ReglaNegocioException("No se puede eliminar: la categoría tiene servicios asociados");
        }
        categoriaRepository.delete(categoria);
    }

    private Categoria buscar(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
    }
}
