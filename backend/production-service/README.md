# production-service

Producción: órdenes de trabajo, cola por prioridad y control de calidad.

## Funcionalidad

- `GET|POST|PUT|DELETE /api/v1/ordenes-produccion` (ADMIN). La cola se ordena ALTA → MEDIA → BAJA y por antigüedad.
- Solo se crea para pedidos CONFIRMADO o EN_DISENO, una por pedido.
- `POST /{id}/inicio` → EN_PROCESO y el pedido pasa a EN_PRODUCCION.
- `POST /{id}/calidad` → aprobado: TERMINADA y pedido TERMINADO; observado: OBSERVADA y luego reproceso.

## Pruebas

- `OrdenProduccionRepositoryTest`, `OrdenProduccionFlujoIntegrationTest`

## Ejecución

Puerto `8097`. Base local `sigip_production` (MySQL); en la nube, esquema propio en PostgreSQL.

Variables: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (o `DB_URL` completa).

```bash
mvn -pl backend/production-service -am spring-boot:run
```
