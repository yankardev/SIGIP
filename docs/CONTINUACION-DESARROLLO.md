# Continuación de SIGIP

## Estado actual

- Backend completo: 8 microservicios + Eureka + API Gateway + librería `sigip-common`.
- `mvn clean verify` desde la raíz: 11 módulos, 78 pruebas en verde.
- Flujo completo probado por el gateway con PostgreSQL (perfil `cloud`): `scripts/prueba-e2e.sh`.
- CI en GitHub Actions (`.github/workflows/ci.yml`), alternativa `Jenkinsfile`.
- Docker: `Dockerfile` único + `docker-compose.yml` (MySQL, RabbitMQ y todos los servicios).
- Nube: `render.yaml` (Render + PostgreSQL). Guía en `docs/09-despliegue-nube.md`.

| Servicio | Puerto | Base local |
|---|---|---|
| discovery-server | 8761 | — |
| api-gateway | 8090 | — |
| auth-service | 8093 | sigip_auth |
| catalog-service | 8094 | sigip_catalog |
| sales-service | 8095 | sigip_sales |
| design-service | 8096 | sigip_design |
| production-service | 8097 | sigip_production |
| inventory-service | 8098 | sigip_inventory |
| notification-service | 8099 | sigip_notification |
| report-service | 8100 | — |

## Al cambiar de máquina

1. Clonar el repositorio y cambiar a `develop`.
2. Opción rápida: `cp .env.example .env` y `docker compose up -d --build`.
3. Opción IntelliJ:
   - Java 25, Maven, MySQL (y RabbitMQ si se quieren eventos).
   - `mysql -u root -p < infra/mysql/init/01-create-databases.sql`
   - Abrir el `pom.xml` de la raíz (proyecto multi-módulo) y ejecutar `mvn clean install -DskipTests`.
   - Variables: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (igual en todos), `SIGIP_ADMIN_PASSWORD` (auth),
     `EVENTOS_HABILITADOS=false` si no hay RabbitMQ.
   - Orden: discovery → auth → catalog → sales → resto → gateway.

## Rutas por el gateway

- Auth: `/auth-service/api/v1/auth/login`, `/auth-service/api/v1/auth/registro`, `/auth-service/api/v1/usuarios`
- Catalog: `/catalog-service/api/v1/categorias`, `/catalog-service/api/v1/servicios`
- Sales: `/sales-service/api/v1/pedidos`, `/sales-service/api/v1/pedidos/{id}/pagos`, `/sales-service/api/v1/pagos/{id}/verificacion`
- Design: `/design-service/api/v1/disenos`
- Production: `/production-service/api/v1/ordenes-produccion`
- Inventory: `/inventory-service/api/v1/insumos`
- Notification: `/notification-service/api/v1/notificaciones`
- Report: `/report-service/api/v1/reportes/dashboard`

## Próximo trabajo sugerido

1. Desplegar en Render siguiendo `docs/09-despliegue-nube.md` y grabar el video de demo.
2. ~~Frontend Angular consumiendo el gateway~~ Hecho en `frontend/` (ver `docs/10-frontend.md`).
3. Proforma en PDF y carga real de archivos de diseño (hoy se registra la URL del archivo).
