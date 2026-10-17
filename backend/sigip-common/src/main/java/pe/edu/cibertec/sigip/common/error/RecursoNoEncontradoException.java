package pe.edu.cibertec.sigip.common.error;

/** El recurso solicitado no existe (o no es visible para el usuario). Se responde 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
