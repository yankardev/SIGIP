package pe.edu.cibertec.sigip.common.error;

/** Un servicio del que dependemos no respondió a tiempo (circuit breaker abierto, timeout). Se responde 503. */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String message) {
        super(message);
    }
}
