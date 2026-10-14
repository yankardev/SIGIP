# SIGIP Web — Frontend Angular

Frontend SPA del **Sistema Integral de Gestión de Impresión y Pedidos (SIGIP)**, conectado al backend de microservicios a través del API Gateway.

## Dos vistas de SIGIP

- **Vista externa (pública):** página de inicio, catálogo de servicios y solicitud de impresión. El catálogo permite seleccionar un servicio y conserva esa selección durante el registro y el inicio de sesión para abrir el formulario del pedido con el servicio preseleccionado.
- **Vista interna (personal):** espacio de trabajo para ventas, caja, diseño, producción, almacén y administración, con navegación filtrada por rol.
- **Seguridad y accesos:** el administrador genera cuentas internas o de cliente, asigna el rol y administra las cuentas. Los guards del frontend mejoran la navegación; las reglas de autorización del backend son las que protegen los endpoints.

## Funcionalidades incluidas

- Inicio de sesión JWT, registro público de clientes, guards de autenticación/roles e interceptor Bearer.
- Dashboard adaptado al portal del cliente o a la gestión interna y catálogo público/interno.
- Registro y seguimiento de pedidos.
- Registro de pago para revisión manual (no procesa dinero ni integra una pasarela).
- CRUD de categorías y servicios para administradores.
- Registro de diseños por URL y aprobación/observación.
- Órdenes de producción, inicio y control de calidad.
- Inventario, entradas/salidas y señal de stock bajo.
- Notificaciones y reportes.

## Instalar en el repositorio

Actualiza la rama `feature/sigip-completo` desde la raíz del repositorio. Abre Git Bash dentro de `frontend/sigip-web/` y ejecuta:

```bash
npm install
npm start
```

Luego abre `http://localhost:4200`. Para compilar producción:

```bash
npm run build
```

## Requisitos

Node.js compatible con Angular 20 (Node 20.19+, 22.12+ o 24), npm y backend SIGIP activo.

## Conexión al backend

La URL base está en `src/environments/environment.ts`:

```ts
apiBaseUrl: 'http://localhost:8090'
```

Si cambias el puerto del gateway, modifica ese valor. En `.env` de la raíz del backend, asegúrate de que CORS incluya `http://localhost:4200` en `CORS_ORIGINS`; conserva los otros orígenes si los necesitas. Después reinicia el gateway:

```bash
docker compose up -d --build api-gateway
```

## Acceso y pruebas

- Cliente: entra por `/` o `/inicio`, explora `/catalogo-publico` y crea una cuenta para registrar el pedido.
- Personal interno: inicia sesión; el menú se filtra por rol: `VENTAS`, `CAJA`, `DISENO`, `PRODUCCION`, `ALMACEN` o `ADMIN`.
- Administrador: usa las credenciales configuradas en `SIGIP_ADMIN_USERNAME`, `SIGIP_ADMIN_EMAIL` y `SIGIP_ADMIN_PASSWORD`. En `/seguridad` puede generar accesos y asignar roles.
- Los roles operativos se crean al arrancar `auth-service`. Reconstruye ese servicio para que se ejecuten los cambios del inicializador.
- El registro de pago solo envía monto, método y número de operación al backend para revisión manual.
- La pantalla de diseños envía el nombre y la URL del archivo. El backend actual no recibe archivos binarios.
- Las pantallas consultan los endpoints reales; si un servicio falla, se muestra un aviso en lugar de inventar datos.

## Endpoints usados a través del gateway

| Funcionalidad | Ruta |
|---|---|
| Login / registro | `/auth-service/api/v1/auth/login`, `/auth-service/api/v1/auth/registro` |
| Categorías / servicios | `/catalog-service/api/v1/categorias`, `/catalog-service/api/v1/servicios` |
| Pedidos / pagos | `/sales-service/api/v1/pedidos` |
| Diseños | `/design-service/api/v1/disenos` |
| Producción | `/production-service/api/v1/ordenes-produccion` |
| Inventario | `/inventory-service/api/v1/insumos` |
| Notificaciones | `/notification-service/api/v1/notificaciones` |
| Dashboard | `/report-service/api/v1/reportes/dashboard` |

## Estructura

```text
src/app/core/
  api.service.ts
  auth.service.ts
  auth.interceptor.ts
  auth.guard.ts
  admin.guard.ts
  role.guard.ts
  models.ts
src/app/pages/
  landing.component.*
  public-catalog.component.*
  login.component.*
  register.component.*
  workspace.component.*
src/app/app.config.ts
src/app/app.routes.ts
```

Usa Angular standalone, `provideRouter()`, `provideHttpClient()` e interceptor funcional; no depende de NgModules.


## Alcance funcional del frontend

La interfaz incluye catálogo, solicitudes/cotizaciones, pedidos, registro y verificación manual de pagos, diseños, producción/calidad, inventario, entregas, usuarios/roles, notificaciones y reportes. No procesa dinero ni integra una pasarela bancaria real. Las cotizaciones se gestionan dentro de `sales-service`; no se crea un microservicio adicional.
