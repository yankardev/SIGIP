package pe.edu.cibertec.sigip.catalogservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cibertec.sigip.catalogservice.model.Categoria;
import pe.edu.cibertec.sigip.catalogservice.model.ServicioImpresion;
import pe.edu.cibertec.sigip.catalogservice.repository.CategoriaRepository;
import pe.edu.cibertec.sigip.catalogservice.repository.ServicioImpresionRepository;
import pe.edu.cibertec.sigip.common.security.JwtService;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Prueba de integración del CRUD REST del catálogo (GET, POST, PUT, DELETE) con seguridad JWT. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogoControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ServicioImpresionRepository servicioRepository;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        servicioRepository.deleteAll();
        categoriaRepository.deleteAll();
        categoria = categoriaRepository.save(Categoria.builder().nombre("Digital").activo(true).build());
        servicioRepository.save(ServicioImpresion.builder().nombre("Volantes").precioBase(new BigDecimal("95.00"))
                .requiereCotizacion(false).activo(true).categoria(categoria).build());
    }

    private String admin() {
        return "Bearer " + jwtService.generarToken("admin", List.of("ROLE_ADMIN"));
    }

    private String cliente() {
        return "Bearer " + jwtService.generarToken("cliente01", List.of("ROLE_CLIENTE"));
    }

    private String json(String nombre, String precio) {
        return """
                {"nombre":"%s","descripcion":"Prueba","precioBase":%s,"requiereCotizacion":false,"idcategoria":%d}"""
                .formatted(nombre, precio, categoria.getIdcategoria());
    }

    @Test
    @DisplayName("GET del catálogo es público")
    void listarSinToken() throws Exception {
        mvc.perform(get("/api/v1/servicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Volantes"))
                .andExpect(jsonPath("$[0].categoria").value("Digital"));
    }

    @Test
    @DisplayName("POST sin token 401, como CLIENTE 403, como ADMIN 201")
    void registrarRequiereAdmin() throws Exception {
        mvc.perform(post("/api/v1/servicios").contentType(MediaType.APPLICATION_JSON).content(json("Tarjetas", "60")))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/servicios").header("Authorization", cliente())
                        .contentType(MediaType.APPLICATION_JSON).content(json("Tarjetas", "60")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/servicios").header("Authorization", admin())
                        .contentType(MediaType.APPLICATION_JSON).content(json("Tarjetas", "60")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idservicio").isNumber());
    }

    @Test
    @DisplayName("PUT actualiza y el GET siguiente refleja el cambio (la caché se invalida)")
    void actualizarInvalidaCache() throws Exception {
        Integer id = servicioRepository.findAll().getFirst().getIdservicio();
        mvc.perform(get("/api/v1/servicios/" + id)).andExpect(jsonPath("$.precioBase").value(95.00));

        mvc.perform(put("/api/v1/servicios/" + id).header("Authorization", admin())
                        .contentType(MediaType.APPLICATION_JSON).content(json("Volantes A5", "99.50")))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/servicios/" + id))
                .andExpect(jsonPath("$.nombre").value("Volantes A5"))
                .andExpect(jsonPath("$.precioBase").value(99.50));
    }

    @Test
    @DisplayName("DELETE elimina y luego GET devuelve 404")
    void eliminar() throws Exception {
        Integer id = servicioRepository.findAll().getFirst().getIdservicio();
        mvc.perform(delete("/api/v1/servicios/" + id).header("Authorization", admin()))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/servicios/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("Validaciones: precio negativo devuelve 400")
    void validaciones() throws Exception {
        mvc.perform(post("/api/v1/servicios").header("Authorization", admin())
                        .contentType(MediaType.APPLICATION_JSON).content(json("Malo", "-5")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("No se elimina una categoría con servicios (409)")
    void categoriaConServicios() throws Exception {
        mvc.perform(delete("/api/v1/categorias/" + categoria.getIdcategoria()).header("Authorization", admin()))
                .andExpect(status().isConflict());
    }
}
