# SIGIP

Sistema Integral de Gestión de Impresión y Pedidos.

## Arquitectura
Eureka :8761 → API Gateway :8090 → auth :8093, catalog :8094, sales :8095, design :8096, production :8097, inventory :8098, notification :8099, report :8100.

## Responsabilidades
- auth: usuarios, roles, BCrypt y JWT.
- catalog: categorías y servicios de impresión.
- sales: pedidos y Feign hacia catálogo.
- design: diseños y aprobaciones.
- production: órdenes de producción y calidad.
- inventory: insumos, stock y movimientos.
- notification: notificaciones y RabbitMQ.
- report: reportes consolidados.
- gateway: entrada HTTP.
- discovery: Eureka.

## MySQL
sigip_auth, sigip_catalog, sigip_sales, sigip_design, sigip_production, sigip_inventory, sigip_notification.

Todos los servicios usan el mismo JWT_SECRET Base64 de auth-service. No subir secretos a Git.

La estructura se crea en develop. La validación local se hará servicio por servicio antes de considerar cada módulo terminado.
