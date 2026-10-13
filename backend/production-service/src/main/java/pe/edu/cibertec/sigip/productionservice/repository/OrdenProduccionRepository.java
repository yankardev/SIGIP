package pe.edu.cibertec.sigip.productionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.cibertec.sigip.productionservice.model.EstadoOrden;
import pe.edu.cibertec.sigip.productionservice.model.OrdenProduccion;

import java.util.List;
import java.util.Optional;

public interface OrdenProduccionRepository extends JpaRepository<OrdenProduccion, Integer> {

    /**
     * Cola de trabajo: primero ALTA, luego MEDIA y BAJA; a igual prioridad, la más antigua primero.
     * (Ordenar por el texto del enum daría un orden alfabético incorrecto.)
     */
    @Query("""
            select o from OrdenProduccion o where o.estado = :estado
            order by case o.prioridad
                when pe.edu.cibertec.sigip.productionservice.model.Prioridad.ALTA then 0
                when pe.edu.cibertec.sigip.productionservice.model.Prioridad.MEDIA then 1
                else 2 end, o.fechaRegistro""")
    List<OrdenProduccion> colaPorEstado(EstadoOrden estado);

    List<OrdenProduccion> findAllByOrderByFechaRegistroDesc();

    Optional<OrdenProduccion> findByIdpedido(Integer idpedido);

    boolean existsByIdpedido(Integer idpedido);
}
