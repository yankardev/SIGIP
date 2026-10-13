# notification-service

Notificaciones: avisos al cliente generados por eventos de pedidos.

## Funcionalidad

- Consume la cola `sigip.notificaciones` (exchange `sigip.pedidos`, routing `pedido.*`).
- Idempotente: un mismo `eventId` no genera dos avisos. Tras 3 reintentos fallidos el mensaje va a `sigip.notificaciones.dlq`.
- `GET /api/v1/notificaciones` (el cliente ve las suyas), `GET /no-leidas`, `PATCH /{id}/leida`.
- POST, PUT y DELETE para ADMIN.
- Sin RabbitMQ: `EVENTOS_HABILITADOS=false`.

## Pruebas

- `NotificacionRepositoryTest`, `NotificacionIntegrationTest` (idempotencia, propiedad, CRUD)

## Ejecución

Puerto `8099`. Base local `sigip_notification` (MySQL); en la nube, esquema propio en PostgreSQL.

Variables: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (o `DB_URL` completa).

```bash
mvn -pl backend/notification-service -am spring-boot:run
```
