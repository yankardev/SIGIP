# Implementación y aceptación

## Estado de entrega

Esta base solo acredita documentación y estructura Git local. Todas las funcionalidades siguientes están pendientes. El porcentaje de avance deberá calcularse a partir de criterios aceptados y evidencias; no a partir del número de carpetas.

## Incrementos

| Incremento | Resultado demostrable | Prioridad |
|---|---|---|
| 0 — base | Alcance, flujo, decisiones, repositorio y backlog | Actual |
| 1 — avance del 10/10/2026 | Auth, BD, BCrypt/JWT, validaciones, repositorios, catálogo CRUD y Angular | Obligatoria |
| 1B — distribución | Eureka, Gateway y consulta Feign real entre servicios | Después de verificar incremento 1 |
| 2 — venta | Cliente, carrito, solicitud, proforma PDF, aceptación y pedido | Siguiente |
| 3 — operación | Pagos verificados, archivos, aprobación de versión, stock y orden de trabajo | Siguiente |
| 4 — mensajería | RabbitMQ, outbox, deduplicación, reintentos y avisos | Siguiente |
| 5 — cierre | Calidad, entrega, reportes, resiliencia, contenedores y observabilidad | Posterior |

Es posible combinar mensajería con el incremento 3 una vez que el comando síncrono funciona. Ningún incremento depende de enviar correos reales para ser demostrable: se puede usar un buzón de prueba identificado como tal.

## Backlog inicial con criterios

| ID | Trabajo | Criterio de aceptación |
|---|---|---|
| SIGIP-01 | Autenticación persistida | Usuario válido entra; clave incorrecta, usuario inexistente/inactivo y token vencido reciben rechazo controlado |
| SIGIP-02 | Usuarios y roles | Password con BCrypt; registro público solo CLIENTE; permisos y propiedad verificados |
| SIGIP-03 | Catálogo | Crear, consultar, actualizar y eliminar categoría/servicio sin referencias; datos persisten al reiniciar |
| SIGIP-04 | Repositorios | Pruebas reales de insertar, actualizar, eliminar y listar con BD aislada |
| SIGIP-05 | Angular | Login, dashboard y CRUD consumen API; errores y sesión se muestran correctamente |
| SIGIP-06 | Eureka/Gateway/Feign | Dos servicios se registran; petición atraviesa Gateway; Feign obtiene datos persistidos del catálogo |
| SIGIP-07 | Proforma | PDF incluye versión/vigencia/especificaciones/total; cambios de tarifa no cambian documento emitido |
| SIGIP-08 | Aceptación | Oferta vencida o sustituida se rechaza; doble clic no duplica pedido |
| SIGIP-09 | Diseño | Cliente solo aprueba versión vigente propia; observaciones y versiones previas conservadas |
| SIGIP-10 | Pago | Comprobante subido queda pendiente hasta verificación de CAJA |
| SIGIP-11 | Reserva | Dos solicitudes concurrentes no sobreasignan stock; timeout se reconcilia por operationId |
| SIGIP-12 | Producción | Falta de pago, diseño o reserva bloquea inicio; calidad observada crea reproceso |
| SIGIP-13 | Eventos | Caída del broker no pierde evento; duplicado no repite efecto; fallo persistente llega a cola de revisión |
| SIGIP-14 | Entrega | Se verifica calidad y saldo; se registra fecha/receptor; cancelación conserva historial |

## Pruebas del negocio que importan

- Repetir aceptación/aprobación/reserva con la misma clave y comprobar un único efecto.
- Intentar consultar y aprobar el pedido de otro cliente.
- Aprobar v1 después de publicar v2: rechazo sin modificar v2.
- Aceptar proforma vencida: rechazo sin crear pedido.
- Dos reservas por más stock del disponible: una no debe obtener confirmación indebida.
- Interrumpir la respuesta de reserva después del guardado: recuperación sin segunda reserva.
- Apagar RabbitMQ al aprobar: decisión guardada y evento pendiente; al restablecerlo, procesamiento.
- Entregar evento duplicado y fuera de orden: sin doble consumo ni regresión de estado.
- Reversar adelanto o invalidar diseño antes de producir: bloqueo de inicio.
- Cancelar después de consumir: sin restitución ficticia de stock.

## Evidencias del primer avance

Informe: diagnóstico SEPTE con al menos tres variables y fuentes, mínimo dos objetivos SMART, justificación y beneficiarios, alcance y conclusiones sustentadas. Evidencias técnicas: solicitudes/respuestas de login, pruebas de repositorios y datos antes/después. Adjuntar capturas solo de ejecución real; no capturas simuladas como evidencia.

El plan oficial asigna 6 puntos al login REST y 6 a las pruebas de insertar, actualizar, eliminar y listar. El alcance general exige Angular y GET/POST/PUT/DELETE persistentes. Los documentos también indican un avance mínimo de 50%; este plan de tareas no acredita por sí solo ese porcentaje.

## Métricas empresariales previstas

Tiempo solicitud→proforma; tasa de aceptación; tiempo aprobación→inicio; pedidos entregados dentro del plazo acordado; reprocesos por causa; consumo y merma real. Las fórmulas y exclusiones deben fijarse antes del piloto. No se declaran ahorros ni mejoras porcentuales sin una línea base medida.
