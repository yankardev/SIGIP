package pe.edu.cibertec.sigip.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Traduce las excepciones a respuestas HTTP coherentes en todos los servicios:
 * 400 datos inválidos, 401 credenciales, 403 permisos, 404 inexistente, 409 regla de negocio, 503 dependencia caída.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Datos inválidos");
        return responder(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> cuerpoInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "El cuerpo de la solicitud no es válido", req);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), req);
    }

    @ExceptionHandler({ReglaNegocioException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> reglaNegocio(RuntimeException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, "BUSINESS_ERROR", ex.getMessage(), req);
    }

    /** Dos usuarios modificaron el mismo registro a la vez (bloqueo optimista con @Version). */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> concurrencia(OptimisticLockingFailureException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, "CONCURRENT_UPDATE",
                "El registro fue modificado por otro usuario; vuelva a consultarlo e intente de nuevo", req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> integridad(DataIntegrityViolationException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, "DATA_INTEGRITY",
                "La operación viola una restricción de datos (duplicado o registro relacionado)", req);
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ApiError> noDisponible(ServicioNoDisponibleException ex, HttpServletRequest req) {
        return responder(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", ex.getMessage(), req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> credenciales(BadCredentialsException ex, HttpServletRequest req) {
        return responder(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Usuario o contraseña incorrectos", req);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> inactivo(DisabledException ex, HttpServletRequest req) {
        return responder(HttpStatus.UNAUTHORIZED, "USER_DISABLED", "El usuario se encuentra inactivo", req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> autenticacion(AuthenticationException ex, HttpServletRequest req) {
        return responder(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_ERROR", "No fue posible autenticar al usuario", req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> accesoDenegado(AccessDeniedException ex, HttpServletRequest req) {
        return responder(HttpStatus.FORBIDDEN, "FORBIDDEN", "No tiene permisos para realizar esta operación", req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> tipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER",
                "Valor no válido para el parámetro '" + ex.getName() + "'", req);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> estado(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return responder(status, status.name(), ex.getReason(), req);
    }

    private ResponseEntity<ApiError> responder(HttpStatus status, String error, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), error, message, req.getRequestURI()));
    }
}
