package pe.edu.cibertec.sigip.inventoryservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cibertec.sigip.common.security.JwtService;
import pe.edu.cibertec.sigip.inventoryservice.repository.InsumoRepository;
import pe.edu.cibertec.sigip.inventoryservice.repository.MovimientoInventarioRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** CRUD de insumos y movimientos de stock por REST. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InsumoIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private InsumoRepository insumoRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    private String admin;

    @BeforeEach
    void setUp() {
        movimientoRepository.deleteAll();
        insumoRepository.deleteAll();
        admin = "Bearer " + jwtService.generarToken("almacen", List.of("ROLE_ADMIN"));
    }

    private Integer crear(String codigo, String stock, String minimo) throws Exception {
        mvc.perform(post("/api/v1/insumos").header("Authorization", admin).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo":"%s","nombre":"Papel couché 115 g","unidadMedida":"PLIEGO",
                                 "stockActual":%s,"stockMinimo":%s}""".formatted(codigo, stock, minimo)))
                .andExpect(status().isCreated());
        return insumoRepository.findAll().stream().filter(i -> i.getCodigo().equals(codigo)).findFirst()
                .orElseThrow().getIdinsumo();
    }

    @Test
    @DisplayName("Entradas y salidas actualizan el stock y quedan en el kárdex; no se permite stock negativo")
    void movimientos() throws Exception {
        Integer id = crear("PAP-115", "100", "30");

        mvc.perform(post("/api/v1/insumos/" + id + "/movimientos").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"SALIDA\",\"cantidad\":80,\"motivo\":\"Pedido 15\",\"idpedido\":15}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockResultante").value(20));

        mvc.perform(get("/api/v1/insumos/bajo-stock").header("Authorization", admin))
                .andExpect(jsonPath("$[0].codigo").value("PAP-115"));

        mvc.perform(post("/api/v1/insumos/" + id + "/movimientos").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"SALIDA\",\"cantidad\":50,\"motivo\":\"Pedido 16\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Stock insuficiente")));

        mvc.perform(post("/api/v1/insumos/" + id + "/movimientos").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"ENTRADA\",\"cantidad\":200,\"motivo\":\"Compra a proveedor\"}"))
                .andExpect(jsonPath("$.stockResultante").value(220));

        mvc.perform(get("/api/v1/insumos/" + id + "/movimientos").header("Authorization", admin))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].usuario").value("almacen"));

        // Con movimientos no se elimina
        mvc.perform(delete("/api/v1/insumos/" + id).header("Authorization", admin)).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("CRUD: PUT edita datos, DELETE elimina, código duplicado 409, validación 400")
    void crud() throws Exception {
        Integer id = crear("TIN-CY", "10", "2");
        crear("TIN-MG", "10", "2");

        mvc.perform(put("/api/v1/insumos/" + id).header("Authorization", admin).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"codigo":"TIN-CY","nombre":"Tinta cian 1L","unidadMedida":"LITRO",
                                 "stockActual":10,"stockMinimo":3}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Tinta cian 1L"));

        mvc.perform(post("/api/v1/insumos").header("Authorization", admin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"TIN-MG\",\"nombre\":\"x\",\"unidadMedida\":\"L\",\"stockActual\":1,\"stockMinimo\":1}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/v1/insumos").header("Authorization", admin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"X\",\"nombre\":\"x\",\"unidadMedida\":\"L\",\"stockActual\":-1,\"stockMinimo\":1}"))
                .andExpect(status().isBadRequest());

        mvc.perform(delete("/api/v1/insumos/" + id).header("Authorization", admin)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/insumos").header("Authorization", admin)).andExpect(jsonPath("$.length()").value(1));
    }
}
