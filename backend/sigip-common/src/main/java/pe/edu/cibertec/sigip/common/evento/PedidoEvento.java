package pe.edu.cibertec.sigip.common.evento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrato del evento que publica sales-service (RabbitMQ) cuando un pedido cambia de estado.
 * notification-service lo consume para registrar el aviso al cliente.
 *
 * @param eventId identificador único, permite descartar mensajes duplicados (idempotencia)
 */
public record PedidoEvento(
        String eventId,
        String tipo,
        Integer idpedido,
        String cliente,
        String estado,
        BigDecimal total,
        LocalDateTime fecha
) {
    public static final String EXCHANGE = "sigip.pedidos";
    public static final String ROUTING_KEY = "pedido.estado";

    public static PedidoEvento de(String tipo, Integer idpedido, String cliente, String estado, BigDecimal total) {
        return new PedidoEvento(UUID.randomUUID().toString(), tipo, idpedido, cliente, estado, total, LocalDateTime.now());
    }
}
