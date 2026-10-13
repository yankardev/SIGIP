package pe.edu.cibertec.sigip.productionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.common.error.RecursoNoEncontradoException;
import pe.edu.cibertec.sigip.common.error.ReglaNegocioException;
import pe.edu.cibertec.sigip.productionservice.client.CambioEstadoPedido;
import pe.edu.cibertec.sigip.productionservice.client.PedidoResumen;
import pe.edu.cibertec.sigip.productionservice.client.SalesServiceClient;
import pe.edu.cibertec.sigip.productionservice.dto.OrdenRequest;
import pe.edu.cibertec.sigip.productionservice.dto.OrdenResponse;
import pe.edu.cibertec.sigip.productionservice.dto.ResultadoCalidadRequest;
import pe.edu.cibertec.sigip.productionservice.model.EstadoOrden;
import pe.edu.cibertec.sigip.productionservice.model.OrdenProduccion;
import pe.edu.cibertec.sigip.productionservice.repository.OrdenProduccionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Reglas de producción:
 * - Solo se crea una orden para un pedido con pago verificado (CONFIRMADO o EN_DISENO) y una sola por pedido.
 * - Iniciar la orden pasa el pedido a EN_PRODUCCION; aprobar calidad lo pasa a TERMINADO.
 * - Si la calidad se observa, la orden vuelve a proceso (reproceso) sin cerrar el pedido.
 */
@Service
@RequiredArgsConstructor
public class OrdenProduccionService {

    private static final Set<String> PEDIDO_LISTO = Set.of("CONFIRMADO", "EN_DISENO");

    private final OrdenProduccionRepository repository;
    private final SalesServiceClient salesClient;

    @Transactional(readOnly = true)
    public List<OrdenResponse> listar(EstadoOrden estado) {
        List<OrdenProduccion> ordenes = estado == null
                ? repository.findAllByOrderByFechaRegistroDesc()
                : repository.colaPorEstado(estado);
        return ordenes.stream().map(OrdenResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrdenResponse obtener(Integer id) {
        return OrdenResponse.from(buscar(id));
    }

    @Transactional
    public OrdenResponse registrar(OrdenRequest request) {
        if (repository.existsByIdpedido(request.idpedido())) {
            throw new ReglaNegocioException("El pedido " + request.idpedido() + " ya tiene una orden de producción");
        }
        PedidoResumen pedido = salesClient.obtenerPedido(request.idpedido());
        if (!PEDIDO_LISTO.contains(pedido.estado())) {
            throw new ReglaNegocioException("El pedido debe tener el pago verificado para producirse (estado actual: "
                    + pedido.estado() + ")");
        }

        OrdenProduccion orden = OrdenProduccion.builder()
                .idpedido(request.idpedido())
                .descripcionTrabajo(pedido.cantidad() + " x " + pedido.nombreServicio())
                .estado(EstadoOrden.PENDIENTE)
                .prioridad(request.prioridad())
                .responsable(request.responsable())
                .observacion(request.observacion())
                .fechaRegistro(LocalDateTime.now())
                .build();
        return OrdenResponse.from(repository.save(orden));
    }

    @Transactional
    public OrdenResponse actualizar(Integer id, OrdenRequest request) {
        OrdenProduccion orden = buscar(id);
        if (orden.getEstado() == EstadoOrden.TERMINADA) {
            throw new ReglaNegocioException("Una orden terminada no se puede modificar");
        }
        if (!orden.getIdpedido().equals(request.idpedido())) {
            throw new ReglaNegocioException("No se puede cambiar el pedido de una orden");
        }
        orden.setPrioridad(request.prioridad());
        orden.setResponsable(request.responsable());
        orden.setObservacion(request.observacion());
        return OrdenResponse.from(repository.save(orden));
    }

    @Transactional
    public void eliminar(Integer id) {
        OrdenProduccion orden = buscar(id);
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new ReglaNegocioException("Solo se puede eliminar una orden PENDIENTE");
        }
        repository.delete(orden);
    }

    @Transactional
    public OrdenResponse iniciar(Integer id) {
        OrdenProduccion orden = buscar(id);
        boolean primeraVez = orden.getEstado() == EstadoOrden.PENDIENTE;
        transicion(orden, EstadoOrden.EN_PROCESO);
        if (primeraVez) {
            orden.setFechaInicio(LocalDateTime.now());
            salesClient.cambiarEstado(orden.getIdpedido(), new CambioEstadoPedido("EN_PRODUCCION", "Producción iniciada"));
        } else {
            orden.setReprocesos(orden.getReprocesos() + 1);
        }
        return OrdenResponse.from(repository.save(orden));
    }

    @Transactional
    public OrdenResponse registrarCalidad(Integer id, ResultadoCalidadRequest request) {
        OrdenProduccion orden = buscar(id);
        if (request.aprobado()) {
            transicion(orden, EstadoOrden.TERMINADA);
            orden.setFechaFin(LocalDateTime.now());
            orden.setObservacion(request.observacion());
            salesClient.cambiarEstado(orden.getIdpedido(), new CambioEstadoPedido("TERMINADO", "Control de calidad aprobado"));
        } else {
            if (request.observacion() == null || request.observacion().isBlank()) {
                throw new ReglaNegocioException("Describa la observación de calidad");
            }
            transicion(orden, EstadoOrden.OBSERVADA);
            orden.setObservacion(request.observacion());
        }
        return OrdenResponse.from(repository.save(orden));
    }

    private void transicion(OrdenProduccion orden, EstadoOrden destino) {
        if (!orden.getEstado().siguientes().contains(destino)) {
            throw new ReglaNegocioException("No se puede pasar de " + orden.getEstado() + " a " + destino);
        }
        orden.setEstado(destino);
    }

    private OrdenProduccion buscar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden de producción no encontrada"));
    }
}
