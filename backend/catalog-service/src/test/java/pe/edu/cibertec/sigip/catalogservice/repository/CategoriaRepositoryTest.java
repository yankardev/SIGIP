package pe.edu.cibertec.sigip.catalogservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;

import static org.assertj.core.api.Assertions.assertThat;

/** Pruebas de la capa de acceso a datos: insertar, actualizar, eliminar y listar categorías. */
@DataJpaTest
@ActiveProfiles("test")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void debeInsertarCategoria() {
        Categoria guardada = categoriaRepository.save(categoria("Gran formato"));

        assertThat(guardada.getIdcategoria()).isNotNull();
        assertThat(categoriaRepository.existsByNombreIgnoreCase("gran FORMATO")).isTrue();
    }

    @Test
    void debeActualizarCategoria() {
        Categoria guardada = categoriaRepository.saveAndFlush(categoria("Digital"));

        guardada.setDescripcion("Impresión digital de pequeño formato");
        categoriaRepository.saveAndFlush(guardada);

        assertThat(categoriaRepository.findById(guardada.getIdcategoria()))
                .get()
                .extracting(Categoria::getDescripcion)
                .isEqualTo("Impresión digital de pequeño formato");
    }

    @Test
    void debeEliminarCategoria() {
        Categoria guardada = categoriaRepository.saveAndFlush(categoria("Temporal"));

        categoriaRepository.deleteById(guardada.getIdcategoria());
        categoriaRepository.flush();

        assertThat(categoriaRepository.findById(guardada.getIdcategoria())).isEmpty();
    }

    @Test
    void debeListarCategorias() {
        categoriaRepository.save(categoria("Digital"));
        categoriaRepository.save(categoria("Offset"));

        assertThat(categoriaRepository.findAll()).hasSize(2);
    }

    private Categoria categoria(String nombre) {
        return Categoria.builder().nombre(nombre).descripcion("Prueba").activo(true).build();
    }
}
