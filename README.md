# SIGIP — Sistema Integral de Gestión de Impresión y Pedidos

Sistema para una imprenta con **backend de microservicios** (Spring Boot 4 y Spring Cloud) y **frontend
en Angular 21**: catálogo, pedidos, pagos, diseños, producción, inventario, notificaciones y reportes.
Proyecto del curso **Desarrollo de Servicios Web II (4698)** — Cibertec, sexto ciclo.

![CI](https://github.com/yankardev/SIGIP/actions/workflows/ci.yml/badge.svg)

## Arquitectura

```
   Navegador ──► ┌──────────────────────┐
                 │ Frontend Angular     │  nginx :4200 (reenvía /api al gateway)
                 └──────────┬───────────┘
                    ┌───────┴──────────────┐
          Postman ─►│  API Gateway  :8090  │  lb:// + CORS + balanceo de carga
                    └──────────┬───────────┘
                               │  (Eureka :8761 registra y ubica las instancias)
   ┌─────────┬─────────┬───────┴──┬──────────┬────────────┬───────────┬──────────┐
 auth     catalog    sales     design   production   inventory  notification  report
 :8093    :8094      :8095     :8096    :8097        :8098      :8099         :8100
 JWT      caché      Feign ──► Feign ──► Feign         kárdex    RabbitMQ ◄──  Feign
 BCrypt   Caffeine   catálogo  ventas    ventas                  (eventos)     ventas+inventario
   │         │         │         │          │            │           │
  BD        BD        BD        BD         BD           BD          BD        (sin BD)
```

- **Base de datos por servicio** (MySQL en local, PostgreSQL en la nube con un esquema por servicio).
- **frontend**: Angular 21 (standalone, signals, lazy loading). Login JWT, menú según rol (ADMIN / CLIENTE)
  y una pantalla por módulo. Detalle en [docs/10-frontend.md](docs/10-frontend.md).
- **sigip-common**: librería compartida con seguridad JWT, formato único de errores y el contrato de eventos.
- **Comunicación síncrona** con OpenFeign + Resilience4j (circuit breaker, timeouts y fallbacks).
- **Comunicación asíncrona** con RabbitMQ: sales publica eventos de pedido; notification los consume con
  reintentos, cola de fallos (DLQ) e idempotencia.

| Servicio | Responsabilidad | Endpoints principales (vía gateway) |
|---|---|---|
| auth | Login JWT, registro, usuarios y roles (BCrypt) | `/auth-service/api/v1/auth/login`, `/auth-service/api/v1/usuarios` |
| catalog | Categorías y servicios de impresión, con caché | `/catalog-service/api/v1/categorias`, `/catalog-service/api/v1/servicios` |
| sales | Pedidos, flujo de estados y pagos | `/sales-service/api/v1/pedidos`, `/sales-service/api/v1/pagos` |
| design | Versiones de diseño y aprobación del cliente | `/design-service/api/v1/disenos` |
| production | Órdenes de producción, cola por prioridad, calidad | `/production-service/api/v1/ordenes-produccion` |
| inventory | Insumos, stock mínimo y kárdex | `/inventory-service/api/v1/insumos` |
| notification | Avisos al cliente desde eventos RabbitMQ | `/notification-service/api/v1/notificaciones` |
| report | Dashboard consolidado del administrador | `/report-service/api/v1/reportes/dashboard` |

Todos los recursos tienen **GET, POST, PUT y DELETE** persistidos en base de datos. Detalle en
[docs/04-datos-y-contratos.md](docs/04-datos-y-contratos.md) y en la colección
[postman/SIGIP.postman_collection.json](postman/SIGIP.postman_collection.json).

## Ejecutar

### Opción 1: Docker Compose (todo el sistema)

```bash
cp .env.example .env          # y cambia las claves
docker compose up -d --build
./scripts/prueba-e2e.sh       # recorre el flujo completo por el gateway
```

**Frontend `http://localhost:4200`** (usuario `admin` / `Admin12345`), gateway `http://localhost:8090`,
Eureka `http://localhost:8761`, RabbitMQ `http://localhost:15672`.

### Opción 2: desde IntelliJ / Maven

Requisitos: Java 25, Maven 3.9, MySQL 8 y (opcional) RabbitMQ.

```bash
mysql -u root -p < infra/mysql/init/01-create-databases.sql
mvn clean install -DskipTests
```

Variables de entorno: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (el mismo en todos) y en auth
`SIGIP_ADMIN_PASSWORD`. Sin RabbitMQ, usa `EVENTOS_HABILITADOS=false`.
Orden de arranque: discovery-server → auth → catalog → sales → resto → api-gateway.

Frontend (Node.js 22 o 24), con el backend ya levantado:

```bash
cd frontend
npm install
npm start        # http://localhost:4200
```

## Pruebas y calidad

```bash
mvn clean verify
```

Compila los 11 módulos y ejecuta **78 pruebas** (repositorios con H2, integración REST con MockMvc,
seguridad, reglas de negocio y la prueba de optimización). Reportes:

- Pruebas: `backend/<servicio>/target/surefire-reports/`
- Cobertura JaCoCo: `backend/<servicio>/target/site/jacoco/index.html`
- Optimización medida: `backend/catalog-service/target/optimizacion-catalogo.md`

Frontend: `cd frontend && npm test` ejecuta **14 pruebas** con Vitest (sesión JWT, interceptor, guards por
rol y manejo de errores). El flujo completo en el navegador está descrito en
[docs/PRUEBAS-MANUALES.md](docs/PRUEBAS-MANUALES.md#11-demo-desde-el-frontend-angular-httplocalhost4200).

## DevOps

- **CI con GitHub Actions** (`.github/workflows/ci.yml`): en cada push o PR a `main`/`develop` compila,
  prueba, publica reportes, prueba y compila el frontend Angular y construye las 11 imágenes Docker
  (10 servicios + frontend). Alternativa en `Jenkinsfile`.
- **Docker**: un solo `Dockerfile` multi-etapa para todos los servicios (`--build-arg SERVICE=...`) y
  `frontend/Dockerfile` (Node para compilar, nginx para servir).
- **Nube**: `render.yaml` despliega todo en Render con PostgreSQL. Ver [docs/09-despliegue-nube.md](docs/09-despliegue-nube.md).
- **Monitoreo**: Actuator (`/actuator/health`) y métricas Prometheus (`/actuator/prometheus`).

## Documentación

| Documento | Contenido |
|---|---|
| [01-alcance](docs/01-alcance.md) | Problema, alcance y actores |
| [02-flujo-operativo](docs/02-flujo-operativo.md) | Flujo del pedido de punta a punta |
| [03-arquitectura](docs/03-arquitectura.md) | Microservicios, patrones y principios SOLID |
| [04-datos-y-contratos](docs/04-datos-y-contratos.md) | Modelo de datos y API |
| [05-plan-y-pruebas](docs/05-plan-y-pruebas.md) | Plan, backlog y pruebas |
| [06-decisiones](docs/06-decisiones.md) | Decisiones de diseño |
| [07-devops-ci-cd](docs/07-devops-ci-cd.md) | Git, pipeline CI, Maven y Docker |
| [08-optimizacion](docs/08-optimizacion.md) | Cuellos de botella, optimización y resultados medidos |
| [09-despliegue-nube](docs/09-despliegue-nube.md) | Publicación en Render con PostgreSQL |
| [10-frontend](docs/10-frontend.md) | Frontend Angular: estructura, pantallas por rol y conexión con el gateway |

## Seguridad

- Contraseñas con BCrypt; nunca en texto plano.
- JWT firmado (HS256) con `JWT_SECRET` compartido; nunca se sube al repositorio (`.env` está en `.gitignore`).
- Roles `ADMIN` y `CLIENTE` con `@PreAuthorize`; un cliente solo ve sus propios pedidos, diseños y avisos.
- En el frontend, un interceptor agrega el token a cada petición y cierra la sesión si expira; los guards
  ocultan las pantallas de administrador (la validación real sigue en el backend).
