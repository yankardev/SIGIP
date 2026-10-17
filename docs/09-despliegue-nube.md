# Despliegue en la nube (Render + PostgreSQL)

La rúbrica recomienda Heroku con PostgreSQL. Heroku ya no tiene plan gratuito, por eso se usa
**Render**, que funciona igual (despliegue desde GitHub, contenedores y PostgreSQL administrado) y
ofrece plan gratuito. Todo está descrito en `render.yaml` (infraestructura como código).

## Qué se despliega

- 1 base PostgreSQL (`sigip-db`). Cada servicio crea y usa su **propio esquema** (`auth`, `catalog`,
  `sales`, ...), así se respeta "una base por servicio" con una sola base gratuita.
- 9 servicios web Docker: el gateway y los 8 microservicios.
- 1 servicio web Docker para el frontend (`sigip-frontend`, nginx) con `API_GATEWAY_URL` apuntando a la URL
  pública del gateway, por ejemplo `https://sigip-gateway.onrender.com`.
- En la nube no hay Eureka ni RabbitMQ: el perfil `cloud` usa **descubrimiento simple** (cada servicio
  conoce las URLs de los otros por variables de entorno) y los eventos se registran en el log.

## Pasos

1. Sube el código a GitHub (rama `main`).
2. En https://dashboard.render.com: **New > Blueprint** y elige el repositorio. Render lee `render.yaml`.
3. Render pide los valores marcados `sync: false`:
   - `SIGIP_ADMIN_PASSWORD` en `sigip-auth` (clave del usuario `admin`).
   - `API_GATEWAY_URL` en `sigip-frontend` (URL pública de `sigip-gateway`).
   - Las URLs públicas de los servicios. Son `https://<nombre>.onrender.com`; por ejemplo
     `CATALOG_URL=https://sigip-catalog.onrender.com`. Si Render agrega un sufijo al nombre, cópialo
     desde la página de cada servicio y actualiza la variable.
4. Espera a que los 9 servicios queden en verde (`/actuator/health` responde `UP`).
5. Prueba el flujo completo contra la nube:

```bash
./scripts/prueba-e2e.sh https://sigip-gateway.onrender.com <clave-admin>
```

## Variables por servicio

| Servicio | Variables |
|---|---|
| todos | `SPRING_PROFILES_ACTIVE=cloud`, `JWT_SECRET` (generada por Render y compartida) |
| con BD | `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (desde `sigip-db`) |
| sigip-gateway | `AUTH_URL`, `CATALOG_URL`, `SALES_URL`, `DESIGN_URL`, `PRODUCTION_URL`, `INVENTORY_URL`, `NOTIFICATION_URL`, `REPORT_URL` |
| sigip-sales | `CATALOG_URL` |
| sigip-design, sigip-production | `SALES_URL` |
| sigip-report | `SALES_URL`, `INVENTORY_URL` |
| sigip-frontend | `API_GATEWAY_URL` |

## Limitaciones del plan gratuito

- Los servicios se duermen tras 15 minutos sin uso; la primera petición tarda cerca de un minuto.
  Antes de la demo, abre `/actuator/health` de cada servicio para despertarlos.
- La base gratuita de Render vence a los 30 días; para la sustentación conviene crearla pocos días antes.

## Verificado localmente

El perfil `cloud` se probó con PostgreSQL 16 levantando los 9 servicios con las mismas variables:
se crearon los esquemas `auth`, `catalog`, `sales`, `design`, `production`, `inventory` y `notification`,
y `scripts/prueba-e2e.sh` completó el flujo login → pedido → pago → producción → dashboard sin errores.

## Otras plataformas

- **Railway**: mismo `Dockerfile`; crear un servicio por microservicio con la variable `SERVICE` y un PostgreSQL.
- **Heroku** (de pago): `heroku container:push web --arg SERVICE=auth-service -a sigip-auth` y el
  add-on Heroku Postgres; la variable `DB_URL` acepta la URL JDBC completa.
