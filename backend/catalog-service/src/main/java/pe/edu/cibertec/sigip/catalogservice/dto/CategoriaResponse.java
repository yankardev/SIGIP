package pe.edu.cibertec.sigip.catalogservice.dto;

import pe.edu.cibertec.sigip.catalogservice.model.Categoria;

public record CategoriaResponse(
        Integer idcategoria,
        String nombre,
        String descripcion,
        Boolean activo
) {

    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getIdcategoria(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getActivo()
        );
    }
}
