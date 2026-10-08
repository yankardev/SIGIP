# SIGIP

**Sistema Integral de Gestión de Impresión y Pedidos**

Plataforma web para una imprenta: conecta el catálogo y la compra del cliente con la cotización, aprobación de diseños, control de materiales, producción y entrega.

## Estado real

Base documental inicial. No contiene todavía una aplicación ejecutable ni acredita funcionalidades implementadas. Los requisitos y contratos son una propuesta de trabajo versionable; las políticas comerciales deben validarse con la imprenta. No se han realizado mediciones ni entrevistas a una empresa.

Repositorio: [yankardev/SIGIP](https://github.com/yankardev/SIGIP), creado por su propietario con visibilidad pública. Esta entrega publica la base documental inicial; la aplicación está pendiente de implementación.

## Qué podrá hacer

- Ofrecer catálogo público, configuración de servicios y carrito de solicitudes.
- Emitir proformas PDF versionadas y convertir una aceptación en un pedido único.
- Registrar adelantos y saldos verificados por caja.
- Recibir archivos, presentar versiones del diseño y registrar aprobación u observaciones del cliente.
- Reservar materiales y programar producción cuando se cumplan los requisitos.
- Registrar impresión, acabados, control de calidad, entrega y trazabilidad.
- Enviar notificaciones sin bloquear el trabajo y mostrar indicadores operativos.

El cliente verá sus pedidos y documentos. El personal trabajará con bandejas por responsabilidad. No se promete facturación electrónica, cobro bancario automático ni envío real de WhatsApp en esta primera versión.

## Documentación

1. [Alcance, actores y reglas](docs/01-alcance.md).
2. [Flujo de trabajo y estados](docs/02-flujo-operativo.md).
3. [Arquitectura y comunicación](docs/03-arquitectura.md).
4. [Modelo de datos y contratos](docs/04-datos-y-contratos.md).
5. [Plan de implementación y aceptación](docs/05-plan-y-pruebas.md).
6. [Fuentes, decisiones y pendientes](docs/06-decisiones.md).

## Organización prevista

Un repositorio contendrá documentación, frontend y proyectos Spring Boot independientes. Un único repositorio no implica un único proceso desplegable.

| Carpeta futura | Responsabilidad |
|---|---|
| `frontend/sigip-web` | Angular: portal del cliente y gestión interna |
| `backend/discovery-server` | Registro Eureka |
| `backend/api-gateway` | Entrada y enrutamiento de API |
| `backend/auth-service` | Usuarios, roles y autenticación |
| `backend/catalog-service` | Catálogo, configuraciones y tarifas |
| `backend/sales-service` | Clientes, proformas, pedidos, caja y entregas |
| `backend/production-service` | Diseños, aprobaciones y órdenes de trabajo |
| `backend/inventory-service` | Insumos, reservas y movimientos |
| `backend/notification-service` | Notificaciones y reintentos |
| `infra` | Despliegue, cuando existan servicios ejecutables |

Estas carpetas se crearán al implementar cada componente. No hay microservicios vacíos presentados como avance.

## Convenciones

- Paquetes Java: `controller`, `dto`, `model`, `repository`, `service`, `security`; `client` y `messaging` cuando correspondan.
- Dinero: `BigDecimal` y columnas decimales. Fechas técnicas en UTC, presentación en `America/Lima`.
- Cada servicio será dueño de sus tablas. Sin consultas SQL ni claves foráneas entre servicios.
- Los secretos se configuran fuera de Git. Los archivos del cliente quedan fuera del repositorio.
- Cada entrega debe distinguir diseño, implementación y evidencia de ejecución.
- Los ejemplos del curso y el informe de referencia permanecen como material de consulta; no se publican dentro de SIGIP.

## Primera meta

Avance del sábado **10 de octubre de 2026**, según la fecha indicada por el responsable del proyecto. Prioridad: login REST, BCrypt, validaciones, persistencia, pruebas de repositorio y Angular. El flujo empresarial completo se desarrolla en incrementos posteriores.
