# report-service

Reportes: dashboard del administrador. No tiene base de datos propia.

## Funcionalidad

- `GET /api/v1/reportes/dashboard` → ventas por estado, ventas confirmadas, pedidos por atender y en taller, insumos críticos.
- `GET /api/v1/reportes/ventas` y `/inventario-critico`.
- Si un servicio no responde, el dashboard llega igual con un aviso (degradación controlada).

## Pruebas

- `ReporteIntegrationTest`, `FallbackTest`

## Ejecución

Puerto `8100`.

```bash
mvn -pl backend/report-service -am spring-boot:run
```
