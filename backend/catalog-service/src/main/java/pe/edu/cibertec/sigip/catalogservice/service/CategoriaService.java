package pe.edu.cibertec.sigip.catalogservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.catalogservice.dto.CategoriaRequest;
import pe.edu.cibertec.sigip.catalogservice.dto.CategoriaResponse;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.repository.CategoriaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(CategoriaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Integer id) {
        return CategoriaResponse.from(
                categoriaRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"))
        );
    }

    @Transactional
    public CategoriaResponse registrar(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = Categoria.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .activo(true)
                .build();

        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Integer id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (!categoria.getNombre().equalsIgnoreCase(request.nombre())
                && categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        categoriaRepository.delete(categoria);
    }
}
