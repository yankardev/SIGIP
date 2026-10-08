# Arquitectura y comunicación

## Organización propuesta

Angular consume las APIs a través del Gateway. Eureka registra servicios y permite descubrimiento; no transporta las solicitudes de negocio. Los servicios REST se estructuran por capas como las prácticas del curso. Cada uno conserva su propia persistencia.

| Servicio | Dueño de datos y comportamiento |
|---|---|
| auth-service | Usuario, rol, vínculo usuario-rol, login BCrypt/JWT |
| catalog-service | Categorías, servicios de impresión, opciones permitidas y tarifas |
| sales-service | Cliente, carrito/solicitud, proforma, pedido, pagos y entrega |
| production-service | Archivos/versiones, decisiones de diseño, orden de trabajo y calidad |
| inventory-service | Insumo, unidad de medida, ficha de consumo, reserva y movimiento |
| notification-service | Avisos, destinatario previsto, canal y resultado de envío |

No se separa cada módulo en un microservicio. Caja y entregas permanecen inicialmente con ventas; diseño y aprobación permanecen con producción. Se extraerían solo ante una necesidad comprobada.

Base propuesta: **MySQL**, por continuidad con la práctica de seguridad recibida. La propuesta previa mencionaba PostgreSQL; la elección MySQL es una decisión de diseño nueva, documentada y revisable antes de implementar. Un servidor local puede alojar bases separadas con usuarios exclusivos para cada servicio.

## Cuándo síncrono y cuándo asíncrono

| Operación | Mecanismo propuesto | Razón |
|---|---|---|
| Login y consulta de catálogo | HTTP REST síncrono | La pantalla necesita respuesta inmediata |
| Validar configuración/tarifa desde ventas | Feign síncrono | No emitir una oferta con datos no validados |
| Aceptar proforma y crear pedido | HTTP síncrono y transacción local en ventas | Confirmar una decisión comercial única |
| Publicar solicitud de revisión | HTTP síncrono para guardar; evento para avisar | Separar persistencia del envío |
| Aprobar una versión de diseño | HTTP síncrono y transacción local en producción | Confirmar qué versión autorizó el cliente |
| Comunicar diseño aprobado | RabbitMQ asíncrono | Actualizar seguimiento y notificar después |
| Reservar materiales | Feign síncrono con clave de operación | Conocer si la reserva fue aceptada; manejar respuesta incierta |
| Crear/programar orden de trabajo | Comando REST con validación de requisitos | No iniciar solo por recibir una notificación |
| Informar producción terminada | Evento asíncrono | Actualizar ventas y avisar al cliente |
| Generar PDF pequeño de proforma | HTTP síncrono bajo demanda | Descargar una versión ya almacenada |
| Enviar correo | Cola asíncrona | El proveedor de correo no condiciona la operación comercial |

La espera humana para revisar un diseño no es una conexión HTTP abierta. El proceso dura horas si es necesario; cada acción individual guarda su resultado en segundos.

## Broker y fiabilidad

Se propone **RabbitMQ** como único broker para el producto inicial. Kafka figura en el temario, pero no se incorpora simultáneamente sin un caso de uso adicional.

Diseño previsto: persistir el cambio de negocio y una fila `outbox_event` en la misma transacción. Un publicador pendiente envía el evento, espera confirmación y marca su publicación. Si el broker cae, la fila permanece pendiente. Este patrón es una ampliación propuesta para fiabilidad, no código acreditado de las prácticas recibidas.

Los consumidores registran `eventId` con una restricción única junto con su cambio local. Confirman recepción después de persistir. Como pueden llegar duplicados, se evita repetir el efecto. Tras reintentos limitados, los mensajes problemáticos pasan a una cola de fallos para revisión. No se promete entrega exactamente una vez ni orden global entre colas.

La documentación de RabbitMQ diferencia confirmación del publicador y reconocimiento del consumidor, y advierte de duplicados al retransmitir: [guía de fiabilidad](https://www.rabbitmq.com/docs/reliability), [confirmaciones](https://www.rabbitmq.com/docs/confirms).

## Consistencia y fallos parciales

- Reservar y consumir son operaciones distintas. El stock disponible es existencia menos reservas activas.
- Inventario aplica una reserva completa de los insumos de una solicitud en transacción local, con control de concurrencia. Si falla uno, no declara la reserva completa.
- Si Feign agota tiempo, ventas consulta el resultado por `operationId`; no repite una reserva con otro identificador.
- Si reservar funcionó pero guardar la confirmación local falló, la conciliación recupera la reserva o solicita liberación. Las reservas previas a programación tienen vencimiento; las asignadas a trabajo activo se gestionan explícitamente.
- Un evento desactualizado no debe reabrir un pedido cancelado. Se comprueban versión del agregado y transición permitida.
- Producción realiza una evaluación de liberación y guarda las versiones de pago, diseño y reserva utilizadas. Las invalidaciones posteriores bloquean el trabajo aún no iniciado.
- Antes de empezar físicamente, se revalidan condiciones. Ante fallos de consulta, se bloquea el inicio. Una carrera entre servicios exige conciliación; no se afirma atomicidad distribuida.
- Cambios/cancelaciones sobre trabajos ya iniciados siguen una orden de cambio y resolución humana, con costos y materiales trazados.

Feign tendrá tiempos límite y circuit breaker. Los reintentos se limitan a lecturas o comandos idempotentes; un fallback no inventa stock ni pagos. [Referencia oficial OpenFeign](https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html).

## Seguridad

Se conserva el enfoque de clase: `SecurityFilterChain`, `UserDetailsService`, BCrypt, filtro JWT y `@PreAuthorize`. Cada servicio valida token y permisos, además del Gateway. JWT y credenciales se externalizan. Usuarios inexistentes, inactivos y claves incorrectas reciben errores controlados sin revelar detalles innecesarios.

Los controles por rol se complementan con propiedad: un CLIENTE no puede leer o aprobar pedidos ajenos cambiando el ID. Las llamadas Feign requieren identidad y autorización; no se hacen públicas las operaciones internas de stock. Los procesos de eventos usan identidades técnicas limitadas cuando necesitan llamar otra API.

Archivos privados con tamaño/tipo permitido y validación de contenido; nombres internos independientes del nombre enviado. Se almacena metadato en BD y binario fuera de Git, con descarga autorizada. Un despliegue real requiere revisión antimalware y almacenamiento con respaldo.

## Despliegue progresivo

Primero ejecución local comprobada. Después Docker Compose para una demostración reproducible; Kubernetes y observabilidad según los objetivos del curso. Se incorporarán Actuator, logs correlacionados y métricas de errores, tiempos y colas. Estas capacidades están planificadas, no implementadas.
