package pe.edu.cibertec.sigip.common.error;

/** Una operación viola una regla del negocio (estado no permitido, stock insuficiente, duplicado...). Se responde 409. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String message) {
        super(message);
    }
}
