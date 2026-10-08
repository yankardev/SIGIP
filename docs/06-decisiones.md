# Decisiones, fuentes y pendientes

Fecha de referencia: 7 de octubre de 2026, zona America/Lima.

## Decisiones de diseño propuestas

| Decisión | Motivo / condición |
|---|---|
| Nombre SIGIP | Elegido expresamente por el responsable del proyecto |
| Repositorio único `yankardev/SIGIP` | Creado por el propietario para mantener juntos documentación y servicios del proyecto |
| Visibilidad pública | Configuración elegida al crear el repositorio; verificada antes de publicar la base documental |
| Portal cliente e interno | Respetar el requisito de ecommerce y el flujo de la imprenta |
| Microservicios con capas internas | Mantener Eureka, Gateway, Feign y convenciones de los ejemplos |
| MySQL propuesto | Coincide con el ejemplo de seguridad y scripts recibidos; revisar antes de implementar si se elige PostgreSQL |
| RabbitMQ propuesto | Un broker para eventos de operación y avisos; no duplicar con Kafka sin necesidad |
| Aprobación síncrona, avisos asíncronos | Confirmar la decisión guardada y desacoplar tareas posteriores |
| Outbox e idempotencia | Extensiones propuestas para tolerar fallos; no afirmar que ya están en el código de clase |
| Pagos verificados manualmente | Tener un proceso demostrable sin simular integración bancaria real |
| Proforma antes de fabricación | Resolver especificaciones y precio de trabajos personalizados |

## Fuentes locales revisadas

| Fuente | Hallazgo / uso |
|---|---|
| FICHA TÉCNICA DEL PROYECTO APLICATIVO.pdf, p. 1 | Ecommerce y entrega de informe, presentación y aplicativo en ZIP |
| 6.- PR 2026 06 Desarrollo de Aplicaciones Web II (4697).pdf, pp. 1–5 | Spring/Angular; login con BCrypt y BD; CRUD REST; estructura de informe; rúbrica de avance |
| Silabo_del_curso.pdf, pp. 3–4 | Feign, RabbitMQ/Kafka, Spring Cloud, seguridad, resiliencia, Docker/Kubernetes y observabilidad |
| Guía_del_curso.pdf | Secuencia de actividades de los temas anteriores |
| Manual_Desarrollo_Aplicaciones_Web_II.pdf | Material independiente de apoyo; no reemplaza las fuentes oficiales |
| RAR de Eureka, Gateway, inventory y order | Se releyeron pom.xml y propiedades para verificar versiones, puertos y enrutamiento |
| app-ventas-security-jwt.rar | Se releyó pom.xml; detalles de seguridad descritos en el contexto previo no equivalen a una auditoría nueva de todas las clases |

Versiones declaradas en los POM recibidos: Java 25, Spring Boot 4.1.1 y, en los cuatro proyectos distribuidos, Spring Cloud 2025.1.3. Son datos del material entregado; todavía no se ha comprobado resolución de dependencias, compatibilidad ni compilación para SIGIP. Se hará antes de fijar el BOM del proyecto.

El Gateway de clase declara rutas HTTP fijas a 8091/8092 con StripPrefix=1; estar registrado en Eureka no convierte esas rutas en balanceadas automáticamente. El cambio futuro a `lb://` será una decisión explícita y verificada.

No se pudo revisar nuevamente `image(5).png` porque no está en la ruta adjunta. Por ello no se utiliza esa captura para afirmar qué temas se completaron. El temario no demuestra por sí solo qué código se ejecutó en clase.

El informe ESFRT se conserva como referencia aportada. Esta base no reproduce su contenido ni atribuye sus diagnósticos a una nueva empresa.

## Fechas

El usuario fijó el primer avance para el sábado 10/10/2026. El plan de proyecto indica AP1 semana 11 y final semana 14; el sílabo ubica el proyecto aplicativo en semana 7. Se conserva la fecha directa del usuario como objetivo operativo, sin resolver artificialmente la discrepancia documental.

## Pendientes del negocio

- Nombre comercial y datos de la imprenta; responsables que validarán el flujo.
- Servicios iniciales y opciones: volantes, tarjetas, banners, etc., según catálogo real.
- Política de adelanto, crédito, cancelación, cambios y reproceso.
- Tarifas, mínimos, tiempos de atención y capacidades de producción.
- Unidades de compra/consumo y fichas de materiales con merma prevista.
- Canales de notificación y datos de prueba autorizados.
- Quién confirma la calidad y quién recibe cada entrega.

Estos puntos no impiden iniciar auth y catálogo; sí impiden presentar las reglas propuestas como políticas ya aprobadas de una empresa real.

## Referencias técnicas primarias

- RabbitMQ, Reliability Guide: https://www.rabbitmq.com/docs/reliability
- RabbitMQ, Consumer Acknowledgements and Publisher Confirms: https://www.rabbitmq.com/docs/confirms
- Spring Cloud OpenFeign, Features: https://docs.spring.io/spring-cloud-openfeign/reference/spring-cloud-openfeign.html

Consulta: 8 de octubre de 2026 UTC / 7 de octubre en Perú. Estas referencias sustentan mecanismos técnicos, no resultados de SIGIP.
