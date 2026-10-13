package pe.edu.cibertec.sigip.authservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pe.edu.cibertec.sigip.authservice.repository.UsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración del servicio web REST de login:
 * levanta auth-service completo sobre H2 y valida respuestas exitosas y de error.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Login correcto devuelve token JWT y roles")
    void loginCorrecto() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomusuario\":\"admin\",\"password\":\"Admin12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.roles[0]").value("ADMIN"));
    }

    @Test
    @DisplayName("La contraseña se almacena cifrada con BCrypt")
    void passwordCifrada() {
        String hash = usuarioRepository.findByNomusuario("admin").orElseThrow().getPassword();
        assertThat(hash).startsWith("$2").isNotEqualTo("Admin12345");
    }

    @Test
    @DisplayName("Contraseña incorrecta devuelve 401")
    void loginPasswordIncorrecta() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomusuario\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("Usuario inexistente devuelve 401 sin revelar detalles")
    void loginUsuarioInexistente() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomusuario\":\"noexiste\",\"password\":\"Admin12345\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Campos vacíos devuelven 400 con mensaje de validación")
    void loginCamposVacios() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomusuario\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Registro público crea un CLIENTE y luego puede iniciar sesión")
    void registroYLogin() throws Exception {
        mvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomusuario":"cliente01","email":"cliente01@sigip.test","password":"Cliente123",
                                 "nombres":"Ana","apellidos":"Pérez"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roles[0]").value("CLIENTE"));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomusuario\":\"cliente01\",\"password\":\"Cliente123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Rutas protegidas: sin token 401, CLIENTE 403, ADMIN 200")
    void controlDeAcceso() throws Exception {
        mvc.perform(get("/api/v1/usuarios")).andExpect(status().isUnauthorized());

        mvc.perform(post("/api/v1/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nomusuario":"cliente02","email":"cliente02@sigip.test","password":"Cliente123",
                         "nombres":"Luis","apellidos":"Rojas"}"""));
        String tokenCliente = token("cliente02", "Cliente123");
        mvc.perform(get("/api/v1/usuarios").header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());

        String tokenAdmin = token("admin", "Admin12345");
        mvc.perform(get("/api/v1/usuarios").header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token alterado es rechazado")
    void tokenAlterado() throws Exception {
        String token = token("admin", "Admin12345");
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token + "x"))
                .andExpect(status().isUnauthorized());
    }

    private String token(String usuario, String password) throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomusuario\":\"" + usuario + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        int inicio = body.indexOf("\"token\":\"") + 9;
        return body.substring(inicio, body.indexOf('"', inicio));
    }
}
