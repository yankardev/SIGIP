# Continuación de SIGIP

## Estado actual en develop

- MySQL: bases `sigip_auth`, `sigip_catalog`, `sigip_sales`, `sigip_design`, `sigip_production`, `sigip_inventory`, `sigip_notification`.
- Discovery Server: 8761.
- Auth Service: 8093.
- Catalog Service: 8094.
- Sales Service: 8095.
- API Gateway: 8090.
- Login JWT probado correctamente mediante Gateway.
- Usuario administrador: `admin`.
- No se almacenan contraseñas en texto plano; se usa BCrypt.
- `JWT_SECRET` debe ser el mismo en auth y en los servicios protegidos.
- No subir secretos al repositorio.

## Al cambiar de máquina

1. Clonar el repositorio.
2. Cambiar a `develop`.
3. Configurar Java 25, Maven, MySQL y RabbitMQ.
4. Crear las bases ejecutando:
   `mysql -u root -p < infra/mysql/init/01-create-databases.sql`
5. Configurar las variables de entorno en IntelliJ:
   - `DB_USER=root`
   - `DB_PASSWORD=<password local de MySQL>`
   - `JWT_SECRET=<mismo secret Base64>`
   - Para auth:
     - `SIGIP_ADMIN_USERNAME=admin`
     - `SIGIP_ADMIN_EMAIL=admin@sigip.local`
     - `SIGIP_ADMIN_PASSWORD=<password local del admin>`
6. Levantar en este orden:
   Discovery -> Auth -> Catalog -> Sales -> servicios restantes -> Gateway.

## Próximo trabajo

1. Validar y corregir la ruta de `sales-service` por Gateway (la última prueba devolvió HTTP 404).
2. Registrar datos reales de categorías y servicios de impresión.
3. Completar la comunicación Sales -> Catalog mediante OpenFeign.
4. Completar Design, Production, Inventory, Notification y Report.
5. Integrar RabbitMQ para eventos/notificaciones.
6. Crear frontend Angular `frontend/sigip-web`.
7. Integrar Angular con API Gateway, JWT y CRUD REST.
8. Completar pruebas de acceso a datos para los módulos principales.
9. Preparar Docker/README, evidencia técnica y entregables académicos.

## Rutas principales

- Auth: `/auth-service/api/v1/auth`
- Catalog: `/catalog-service/api/v1/servicios`
- Sales: `/sales-service/api/v1/pedidos`
- Design: `/design-service/api/v1/disenos`
- Production: `/production-service/api/v1/ordenes-produccion`
- Inventory: `/inventory-service/api/v1/insumos`
- Notification: `/notification-service/api/v1/notificaciones`
- Report: `/report-service/api/v1/reportes/estado`

> Este documento es un punto de control para continuar el desarrollo desde otra máquina.
