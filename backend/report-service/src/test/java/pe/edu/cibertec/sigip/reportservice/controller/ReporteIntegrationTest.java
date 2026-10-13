package pe.edu.cibertec.sigip.reportservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.cibertec.sigip.common.error.ServicioNoDisponibleException;
import pe.edu.cibertec.sigip.common.security.JwtService;
import pe.edu.cibertec.sigip.reportservice.client.InsumoCritico;
import pe.edu.cibertec.sigip.reportservice.client.InventoryServiceClient;
import pe.edu.cibertec.sigip.reportservice.client.ResumenVentas;
import pe.edu.cibertec.sigip.reportservice.client.SalesServiceClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReporteIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtService jwtService;
    @MockitoBean
    private SalesServiceClient sales;
    @MockitoBean
    private InventoryServiceClient inventory;

    private String admin;

    @BeforeEach
    void setUp() {
        admin = "Bearer " + jwtService.generarToken("admin", List.of("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("El dashboard consolida ventas e insumos críticos")
    void dashboardCompleto() throws Exception {
        when(sales.resumen()).thenReturn(new ResumenVentas(6,
                Map.of("REGISTRADO", 2L, "CONFIRMADO", 1L, "EN_PRODUCCION", 2L, "ENTREGADO", 1L),
                new BigDecimal("380.00")));
        when(inventory.bajoStock()).thenReturn(List.of(new InsumoCritico(1, "PAP-115", "Papel couché", "PLIEGO",
                new BigDecimal("20"), new BigDecimal("30"))));

        mvc.perform(get("/api/v1/reportes/dashboard").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventas.ventasConfirmadas").value(380.00))
                .andExpect(jsonPath("$.pedidosPorAtender").value(3))
                .andExpect(jsonPath("$.pedidosEnTaller").value(2))
                .andExpect(jsonPath("$.insumosBajoStock[0].codigo").value("PAP-115"))
                .andExpect(jsonPath("$.avisos.length()").value(1));
    }

    @Test
    @DisplayName("Si ventas no responde, el dashboard se degrada y lo informa; /ventas responde 503")
    void degradacion() throws Exception {
        when(sales.resumen()).thenThrow(new ServicioNoDisponibleException("El servicio de ventas no está disponible en este momento"));
        when(inventory.bajoStock()).thenReturn(List.of());

        mvc.perform(get("/api/v1/reportes/dashboard").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventas").doesNotExist())
                .andExpect(jsonPath("$.avisos[0]").value("El servicio de ventas no está disponible en este momento"));

        mvc.perform(get("/api/v1/reportes/ventas").header("Authorization", admin))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("Los reportes son solo para ADMIN")
    void soloAdmin() throws Exception {
        String cliente = "Bearer " + jwtService.generarToken("ana", List.of("ROLE_CLIENTE"));
        mvc.perform(get("/api/v1/reportes/dashboard").header("Authorization", cliente))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/reportes/dashboard")).andExpect(status().isUnauthorized());
    }
}
