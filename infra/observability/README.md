# Observabilidad local de SIGIP

Esta configuración añade Prometheus y Grafana al entorno local existente sin reconstruir ni detener los microservicios.

## Requisitos

- SIGIP levantado con Docker Compose.
- La red Docker externa `sigip_default` ya creada.
- Los servicios `sales-service` y `catalog-service` accesibles por esa red y con `/actuator/prometheus` habilitado.

## Arranque

Desde la raíz del repositorio, en Git Bash:

```bash
export GRAFANA_ADMIN_PASSWORD='cambia-esta-clave'
docker compose -f infra/observability/docker-compose.yml up -d
```

No es necesario ejecutar `docker compose down` ni recrear los servicios de SIGIP.

## Direcciones

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (usuario `admin`; contraseña la definida en `GRAFANA_ADMIN_PASSWORD`).

Prometheus consulta métricas de `sales-service:8095` y `catalog-service:8094` cada 15 segundos. En Prometheus, revisa **Status → Targets**. En Grafana, la fuente Prometheus se registra automáticamente.

## Qué se puede observar

- Estado y disponibilidad de los servicios.
- Volumen de solicitudes y errores HTTP si las métricas están expuestas.
- Latencia HTTP y recursos JVM mediante métricas Actuator/Micrometer.

Los nombres exactos de las métricas dependen de la configuración de Spring Boot y Micrometer. Verifícalos en Prometheus antes de construir consultas o dashboards.

## Límites actuales

- Este montaje agrega Prometheus y Grafana; todavía no incorpora Elasticsearch, Logstash y Kibana (ELK).
- No crea dashboards de negocio predefinidos.
- Si el servicio de la aplicación no está conectado a `sigip_default`, el target aparecerá como DOWN.
