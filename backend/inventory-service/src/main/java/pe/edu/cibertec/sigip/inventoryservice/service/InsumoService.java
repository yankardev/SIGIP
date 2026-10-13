package pe.edu.cibertec.sigip.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.sigip.common.error.RecursoNoEncontradoException;
import pe.edu.cibertec.sigip.common.error.ReglaNegocioException;
import pe.edu.cibertec.sigip.inventoryservice.dto.*;
import pe.edu.cibertec.sigip.inventoryservice.model.Insumo;
import pe.edu.cibertec.sigip.inventoryservice.model.MovimientoInventario;
import pe.edu.cibertec.sigip.inventoryservice.model.TipoMovimiento;
import pe.edu.cibertec.sigip.inventoryservice.repository.InsumoRepository;
import pe.edu.cibertec.sigip.inventoryservice.repository.MovimientoInventarioRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reglas de inventario:
 * - El stock solo cambia mediante movimientos (ENTRADA/SALIDA), que quedan en el kárdex.
 * - Una salida no puede dejar stock negativo.
 * - Un insumo con movimientos no se elimina (se desactiva) para no perder la trazabilidad.
 */
@Service
@RequiredArgsConstructor
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Transactional(readOnly = true)
    public List<InsumoResponse> listar() {
        return insumoRepository.findAllByOrderByNombreAsc().stream().map(InsumoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<InsumoResponse> bajoStock() {
        return insumoRepository.bajoStock().stream().map(InsumoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public InsumoResponse obtener(Integer id) {
        return InsumoResponse.from(buscar(id));
    }

    @Transactional
    public InsumoResponse registrar(InsumoRequest request) {
        if (insumoRepository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new ReglaNegocioException("Ya existe un insumo con el código " + request.codigo());
        }
        Insumo insumo = Insumo.builder()
                .codigo(request.codigo().toUpperCase())
                .nombre(request.nombre())
                .unidadMedida(request.unidadMedida())
                .stockActual(request.stockActual())
                .stockMinimo(request.stockMinimo())
                .activo(request.activo() == null || request.activo())
                .build();
        return InsumoResponse.from(insumoRepository.save(insumo));
    }

    /** Edita los datos maestros. El stock NO se edita aquí: se usa un movimiento para dejar rastro. */
    @Transactional
    public InsumoResponse actualizar(Integer id, InsumoRequest request) {
        Insumo insumo = buscar(id);
        if (!insumo.getCodigo().equalsIgnoreCase(request.codigo())
                && insumoRepository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new ReglaNegocioException("Ya existe un insumo con el código " + request.codigo());
        }
        insumo.setCodigo(request.codigo().toUpperCase());
        insumo.setNombre(request.nombre());
        insumo.setUnidadMedida(request.unidadMedida());
        insumo.setStockMinimo(request.stockMinimo());
        if (request.activo() != null) {
            insumo.setActivo(request.activo());
        }
        return InsumoResponse.from(insumoRepository.save(insumo));
    }

    @Transactional
    public void eliminar(Integer id) {
        Insumo insumo = buscar(id);
        if (movimientoRepository.existsByInsumoIdinsumo(id)) {
            throw new ReglaNegocioException("El insumo tiene movimientos registrados; desactívelo en lugar de eliminarlo");
        }
        insumoRepository.delete(insumo);
    }

    @Transactional
    public MovimientoResponse registrarMovimiento(Integer id, MovimientoRequest request, String usuario) {
        Insumo insumo = buscar(id);
        if (!insumo.getActivo()) {
            throw new ReglaNegocioException("El insumo está desactivado");
        }

        BigDecimal nuevoStock = request.tipo() == TipoMovimiento.ENTRADA
                ? insumo.getStockActual().add(request.cantidad())
                : insumo.getStockActual().subtract(request.cantidad());
        if (nuevoStock.signum() < 0) {
            throw new ReglaNegocioException("Stock insuficiente de " + insumo.getNombre() + ": disponible "
                    + insumo.getStockActual() + " " + insumo.getUnidadMedida());
        }
        insumo.setStockActual(nuevoStock);
        insumoRepository.save(insumo);

        MovimientoInventario movimiento = movimientoRepository.save(MovimientoInventario.builder()
                .insumo(insumo)
                .tipo(request.tipo())
                .cantidad(request.cantidad())
                .stockResultante(nuevoStock)
                .motivo(request.motivo())
                .idpedido(request.idpedido())
                .usuario(usuario)
                .fecha(LocalDateTime.now())
                .build());
        return MovimientoResponse.from(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> kardex(Integer id) {
        buscar(id);
        return movimientoRepository.findByInsumoIdinsumoOrderByFechaDesc(id).stream()
                .map(MovimientoResponse::from).toList();
    }

    private Insumo buscar(Integer id) {
        return insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Insumo no encontrado"));
    }
}
