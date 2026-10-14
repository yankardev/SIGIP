package pe.edu.cibertec.sigip.common.error;

import java.time.LocalDateTime;

/** Formato único de error que devuelven todos los servicios de SIGIP. */
public record ApiError(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(status, error, message, path, LocalDateTime.now());
    }
}
