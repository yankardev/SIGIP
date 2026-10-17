# Optimización de la arquitectura: diagnóstico, cambios y resultados medidos

## 1. Cuellos de botella encontrados en la versión inicial

| # | Problema | Dónde | Impacto |
|---|---|---|---|
| 1 | Consulta N+1: listar servicios hacía 1 consulta + 1 por cada categoría (LAZY) | catalog-service | El tiempo crece con el número de categorías |
| 2 | El catálogo se leía de la base de datos en cada petición, aunque casi no cambia | catalog-service | Carga innecesaria en la BD |
| 3 | Seguridad JWT, manejo de errores y filtros copiados en cada servicio | todos | Código duplicado, respuestas de error distintas |
| 4 | Gateway con URLs fijas (`http://localhost:8094`) | api-gateway | No permite escalar ni balancear carga |
| 5 | Llamadas Feign sin timeout ni circuit breaker | sales, design, production | Un servicio caído bloquea a los demás |
| 6 | `open-in-view` activo y SQL impreso siempre | todos | Conexiones retenidas y logs pesados |
| 7 | Sin índices en columnas de búsqueda; sin control de concurrencia | sales, production, inventory | Búsquedas lentas y actualizaciones perdidas |
| 8 | auth-service no compilaba (faltaba un import) | auth-service | El sistema no arrancaba |

## 2. Cambios aplicados

| # | Optimización | Patrón / técnica |
|---|---|---|
| 1 | `JOIN FETCH` en `listarConCategoria` y `listarActivosConCategoria` | Refactorización de consultas |
| 2 | Caché Caffeine con `@Cacheable` / `@CacheEvict` (500 entradas, 10 min) | Cache-aside |
| 3 | Librería `sigip-common` autoconfigurada (seguridad, errores, eventos) | DRY, Single Responsibility |
| 4 | Rutas `lb://` resueltas por Eureka + Spring Cloud LoadBalancer | Balanceo de carga (round robin) |
| 5 | Resilience4j: timeouts de 2-3 s, circuit breaker y fallbacks; el dashboard se degrada en vez de fallar | Circuit Breaker, Fallback |
| 6 | Cliente HTTP Apache HC5 en Feign (pool de conexiones y soporte de PATCH) | Reutilización de conexiones |
| 7 | Índices (`cliente`, `estado`), restricciones únicas y `@Version` (bloqueo optimista) | Optimización de BD |
| 8 | Eventos RabbitMQ después del commit, idempotencia por `eventId` y DLQ | Event-driven, Idempotent consumer |
| 9 | `open-in-view=false`, `show-sql` solo con `SHOW_SQL=true`, pool Hikari ajustado en la nube | Configuración |
| 10 | Actuator + Prometheus en todos los servicios | Observabilidad |

## 3. Resultados medidos

### 3.1 Consulta del catálogo (prueba automatizada `OptimizacionCatalogoTest`)

Se ejecuta en cada build (`mvn verify`) y deja el reporte en `backend/catalog-service/target/optimizacion-catalogo.md`.
Datos: 200 servicios en 20 categorías; promedio de 50 ejecuciones. Las consultas se cuentan con las estadísticas de Hibernate.

| Escenario | Consultas SQL por listado | Tiempo promedio |
|---|---:|---:|
| Antes: `findAll()` + categoría LAZY (N+1) | 21 | 9.7 ms |
| Después 1: `JOIN FETCH` | 1 | 2.2 ms |
| Después 2: `JOIN FETCH` + caché Caffeine | 0 | 0.07 ms |

**Resultado:** 21 veces menos consultas y una respuesta más de 100 veces más rápida en la capa de datos.

### 3.2 Prueba de carga por el API Gateway (`scripts/prueba-carga.py`)

Sistema completo levantado con perfil `cloud` sobre PostgreSQL 16. Endpoint
`GET /catalog-service/api/v1/servicios/activos` con 210 servicios; 3000 peticiones con 20 usuarios concurrentes.

| Escenario | Rendimiento | Latencia p50 | Latencia p95 | Latencia p99 | Errores |
|---|---:|---:|---:|---:|---:|
| Sin caché (`SPRING_CACHE_TYPE=none`) | 404 req/s | 41.6 ms | 116.0 ms | 165.1 ms | 0 |
| Con caché Caffeine | 627 req/s | 29.2 ms | 59.3 ms | 79.6 ms | 0 |
| **Mejora** | **+55 %** | **-30 %** | **-49 %** | **-52 %** | |

La mejora de punta a punta es menor que en la capa de datos porque cada petición también pasa por el
gateway, la validación del JWT y la serialización JSON; aun así la latencia p95 se reduce a la mitad.

Para repetir la medición:

```bash
python3 scripts/prueba-carga.py http://localhost:8090/catalog-service/api/v1/servicios/activos 3000 20
```

### 3.3 Métricas para monitoreo

`GET /actuator/prometheus` en catalog-service expone `cache_gets_total{result="hit|miss"}` (tasa de aciertos
de la caché) y `http_server_requests_seconds` (latencia por endpoint), listas para Prometheus/Grafana.

## 4. Conclusión

Los dos cambios de mayor impacto fueron eliminar el N+1 y cachear el catálogo, porque el catálogo es la
consulta más frecuente y la que menos cambia. Los cambios de resiliencia (timeouts, circuit breaker, DLQ)
no aceleran una petición, pero evitan que la caída de un servicio se propague al resto del sistema.
