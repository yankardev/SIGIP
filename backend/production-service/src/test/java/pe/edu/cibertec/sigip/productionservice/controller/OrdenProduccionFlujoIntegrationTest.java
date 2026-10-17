package pe.edu.cibertec.sigip.productionservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cibertec.sigip.common.security.JwtService;
import pe.edu.cibertec.sigip.productionservice.client.CambioEstadoPedido;
import pe.edu.cibertec.sigip.productionservice.client.PedidoResumen;
import pe.edu.cibertec.sigip.productionservice.client.SalesServiceClient;
import pe.edu.cibertec.sigip.productionservice.repository.OrdenProduccionRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Flujo de producción con control de calidad y sincronización del estado del pedido en ventas. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrdenProduccionFlujoIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private OrdenProduccionRepository repository;
    @MockitoBean
    private SalesServiceClient salesClient;

    private String admin;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        admin = "Bearer " + jwtService.generarToken("jefe.produccion", List.of("ROLE_ADMIN"));
        when(salesClient.obtenerPedido(20)).thenReturn(new PedidoResumen(20, "cliente01", "Volantes", 2, "CONFIRMADO"));
        when(salesClient.obtenerPedido(21)).thenReturn(new PedidoResumen(21, "cliente01", "Banner", 1, "REGISTRADO"));
    }

    private Integer crearOrden() throws Exception {
        mvc.perform(post("/api/v1/ordenes-produccion").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"idpedido\":20,\"prioridad\":\"ALTA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcionTrabajo").value("2 x Volantes"));
        return repository.findByIdpedido(20).orElseThrow().getIdorden();
    }

    @Test
    @DisplayName("Inicio, observación de calidad, reproceso y cierre sincronizan el pedido")
    void flujoConReproceso() throws Exception {
        Integer id = crearOrden();

        mvc.perform(post("/api/v1/ordenes-produccion/" + id + "/inicio").header("Authorization", admin))
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
        verify(salesClient).cambiarEstado(eq(20), eq(new CambioEstadoPedido("EN_PRODUCCION", "Producción iniciada")));

        mvc.perform(post("/api/v1/ordenes-produccion/" + id + "/calidad").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"aprobado\":false,\"observacion\":\"Color desfasado\"}"))
                .andExpect(jsonPath("$.estado").value("OBSERVADA"));

        mvc.perform(post("/api/v1/ordenes-produccion/" + id + "/inicio").header("Authorization", admin))
                .andExpect(jsonPath("$.reprocesos").value(1));

        mvc.perform(post("/api/v1/ordenes-produccion/" + id + "/calidad").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"aprobado\":true}"))
                .andExpect(jsonPath("$.estado").value("TERMINADA"));
        verify(salesClient).cambiarEstado(eq(20), eq(new CambioEstadoPedido("TERMINADO", "Control de calidad aprobado")));
    }

    @Test
    @DisplayName("Reglas: pedido sin pago (409), orden duplicada (409), salto de estado (409), CLIENTE (403)")
    void reglas() throws Exception {
        mvc.perform(post("/api/v1/ordenes-produccion").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"idpedido\":21,\"prioridad\":\"BAJA\"}"))
                .andExpect(status().isConflict());

        Integer id = crearOrden();
        mvc.perform(post("/api/v1/ordenes-produccion").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"idpedido\":20,\"prioridad\":\"BAJA\"}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/v1/ordenes-produccion/" + id + "/calidad").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"aprobado\":true}"))
                .andExpect(status().isConflict());

        String cliente = "Bearer " + jwtService.generarToken("cliente01", List.of("ROLE_CLIENTE"));
        mvc.perform(get("/api/v1/ordenes-produccion").header("Authorization", cliente))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT edita prioridad y DELETE solo en PENDIENTE")
    void editarYEliminar() throws Exception {
        Integer id = crearOrden();
        mvc.perform(put("/api/v1/ordenes-produccion/" + id).header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idpedido\":20,\"prioridad\":\"MEDIA\",\"responsable\":\"Operario 2\"}"))
                .andExpect(jsonPath("$.prioridad").value("MEDIA"))
                .andExpect(jsonPath("$.responsable").value("Operario 2"));
        mvc.perform(get("/api/v1/ordenes-produccion?estado=PENDIENTE").header("Authorization", admin))
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete("/api/v1/ordenes-produccion/" + id).header("Authorization", admin))
                .andExpect(status().isNoContent());
    }
}
