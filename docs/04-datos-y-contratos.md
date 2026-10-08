# Datos y contratos preliminares

## Entidades por servicio

| Servicio | Entidades principales |
|---|---|
| Autenticación | Usuario, Rol, UsuarioRol |
| Catálogo | Categoria, ServicioImpresion, OpcionConfiguracion, CombinacionPermitida, TarifaVersion |
| Ventas | Cliente, Carrito, SolicitudCotizacion, Proforma, ProformaVersion, DetalleProforma, Pedido, DetallePedido, Pago, Entrega, HistorialPedido |
| Producción | ArchivoTrabajo, VersionDiseno, DecisionDiseno, OrdenProduccion, EtapaProduccion, ControlCalidad, OrdenCambio |
| Inventario | Insumo, UnidadMedida, FichaConsumoVersion, DetalleConsumo, Reserva, DetalleReserva, MovimientoInventario |
| Notificaciones | Notificacion, IntentoEnvio |
| Servicios con eventos | OutboxEvent, EventoProcesado según su papel |

Dentro de cada servicio se usan relaciones JPA y claves foráneas. Entre servicios solo identificadores y contratos REST/eventos. El pedido conserva una copia de descripción, especificaciones, precio, impuestos configurados y tarifa utilizada; editar el catálogo no altera una venta acordada.

Restricciones clave: username/email únicos; pedido único por proforma aceptada; decisión única por versión y operación; clave de reserva única; movimiento de consumo único por operación; `eventId` único por consumidor. Versionado optimista para evitar sobrescrituras concurrentes.

## API propuesta

Rutas de servicio; el Gateway añadirá un prefijo por servicio y lo retirará al enrutar, siguiendo la práctica recibida.

| Método y ruta | Servicio | Resultado esperado |
|---|---|---|
| POST `/api/v1/auth/login` | auth | Token y datos mínimos de sesión |
| POST `/api/v1/auth/registro` | auth | Alta de CLIENTE; nunca roles recibidos del navegador |
| GET/POST `/api/v1/categorias` | catalog | Listado o alta autorizada |
| GET/PUT/DELETE `/api/v1/categorias/{id}` | catalog | Consulta, edición o borrado permitido |
| GET/POST `/api/v1/servicios` | catalog | Catálogo o alta autorizada |
| GET/PUT/DELETE `/api/v1/servicios/{id}` | catalog | Consulta, edición o eliminación de registro no referenciado |
| POST `/api/v1/solicitudes` | sales | Solicitud de cotización |
| POST `/api/v1/proformas` | sales | Proforma en borrador |
| POST `/api/v1/proformas/{id}/emitir` | sales | Versión numerada e inmutable |
| GET `/api/v1/proformas/{id}/versiones/{version}/pdf` | sales | PDF de la versión autorizada |
| POST `/api/v1/proformas/{id}/aceptaciones` | sales | Aceptación y pedido único |
| GET `/api/v1/pedidos/{id}` | sales | Pedido propio o autorizado |
| POST `/api/v1/pedidos/{id}/pagos` | sales | Pago pendiente de verificación |
| POST `/api/v1/pagos/{id}/verificacion` | sales | Decisión de CAJA con evidencia |
| POST `/api/v1/pedidos/{id}/disenos` | production | Nueva versión de diseño |
| POST `/api/v1/disenos/{id}/decisiones` | production | APROBAR u OBSERVAR versión vigente |
| POST `/api/v1/reservas` | inventory | Reserva atómica o falta de stock |
| GET `/api/v1/reservas/operaciones/{operationId}` | inventory | Consulta de resultado incierto |
| POST `/api/v1/ordenes-produccion` | production | Orden ligada a pedido, sin duplicación |
| POST `/api/v1/ordenes-produccion/{id}/liberacion` | production | Evaluación de pago, diseño y reserva |
| POST `/api/v1/ordenes-produccion/{id}/inicio` | production | Revalidación e inicio autorizado |
| POST `/api/v1/pedidos/{id}/entregas` | sales | Entrega con controles de saldo/calidad |

Los cambios de estado usan acciones de negocio; no un PUT libre que permita marcar un pedido como ENTREGADO saltándose controles.

## Comando de aprobación — ejemplo sintético

```http
POST /api/v1/disenos/42/decisiones
Authorization: Bearer <token>
Idempotency-Key: <uuid-de-la-operacion>
Content-Type: application/json

{"version":2,"decision":"APROBAR","comentario":"Conforme con la prueba presentada"}
```

El servidor obtiene actor del token, no del JSON. Responde 201 con el registro persistido; una repetición idéntica puede devolver 200 con el mismo resultado. Una clave reutilizada con otro contenido o una versión sustituida devuelve 409. Un fallo de notificación posterior no convierte la aprobación guardada en error.

## Eventos propuestos

| Evento | Emisor | Consumidores y finalidad |
|---|---|---|
| `proforma.emitida.v1` | sales | notification: aviso de oferta disponible |
| `pedido.confirmado.v1` | sales | production: preparar expediente; notification: confirmación |
| `diseno.revision-solicitada.v1` | production | notification: aviso para revisar |
| `diseno.aprobado.v1` | production | sales: seguimiento; notification: aviso interno |
| `diseno.invalidado.v1` | production | sales: reflejar revisión pendiente |
| `pago.validado.v1` / `pago.reversado.v1` | sales | production: actualizar condición financiera y bloqueo |
| `reserva.confirmada.v1` / `reserva.liberada.v1` / `reserva.vencida.v1` | inventory | sales y production: disponibilidad y bloqueo |
| `produccion.terminada.v1` | production | sales: habilitar preparación de entrega; notification: aviso |
| `pedido.cancelado.v1` | sales | inventory y production: aplicar transición permitida/conciliar |
| `pedido.entregado.v1` | sales | notification: confirmación final |

Envoltorio propuesto: `eventId`, `eventType`, `schemaVersion`, `aggregateId`, `aggregateVersion`, `occurredAt`, `correlationId`, `payload`. Incluir identificadores y datos mínimos, nunca contraseñas, tokens ni archivos completos. Las versiones se ordenan por agregado y por emisor, no por una secuencia global.

## Respuestas y visualización

400: formato inválido; 401: falta autenticación válida; 403: falta permiso; 404: recurso no visible/inexistente según política; 409: conflicto de estado, stock o versión; 503: dependencia temporalmente indisponible. La interfaz debe distinguir «aprobación guardada» de «aviso pendiente». El seguimiento alimentado por eventos muestra fecha de actualización y puede tener demora.
