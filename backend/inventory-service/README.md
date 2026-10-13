# inventory-service

Inventario: insumos, stock mínimo y kárdex de movimientos.

## Funcionalidad

- `GET|POST|PUT|DELETE /api/v1/insumos` (ADMIN).
- El stock cambia solo con movimientos: `POST /api/v1/insumos/{id}/movimientos` (ENTRADA/SALIDA); nunca queda negativo (409).
- `GET /api/v1/insumos/bajo-stock` → insumos en o bajo el mínimo.
- Bloqueo optimista (`@Version`) para actualizaciones concurrentes.

## Pruebas

- `InsumoRepositoryTest`, `InsumoIntegrationTest`

## Ejecución

Puerto `8098`. Base local `sigip_inventory` (MySQL); en la nube, esquema propio en PostgreSQL.

Variables: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (o `DB_URL` completa).

```bash
mvn -pl backend/inventory-service -am spring-boot:run
```
