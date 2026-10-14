package pe.edu.cibertec.sigip.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Escribe respuestas 401 y 403 en JSON cuando el error ocurre antes de llegar al controlador. */
public final class RespuestasSeguridad {

    private RespuestasSeguridad() {
    }

    public static void noAutenticado(HttpServletRequest request, HttpServletResponse response,
                                     AuthenticationException ex) throws IOException {
        escribir(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED",
                "Debe iniciar sesión con un token válido", request.getRequestURI());
    }

    public static void accesoDenegado(HttpServletRequest request, HttpServletResponse response,
                                      AccessDeniedException ex) throws IOException {
        escribir(response, HttpStatus.FORBIDDEN, "FORBIDDEN",
                "No tiene permisos para realizar esta operación", request.getRequestURI());
    }

    private static void escribir(HttpServletResponse response, HttpStatus status, String error,
                                 String message, String path) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = """
                {"status":%d,"error":"%s","message":"%s","path":"%s"}"""
                .formatted(status.value(), error, message, path.replace("\"", ""));
        response.getWriter().write(json);
    }
}
