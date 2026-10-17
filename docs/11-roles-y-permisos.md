# Roles y permisos de SIGIP

Este documento define la matriz inicial de permisos para que la aplicación coincida con el informe del proyecto. El backend es la autoridad de seguridad; ocultar opciones en Angular no sustituye la autorización de la API.

## Roles

| Rol | Responsabilidad principal |
|---|---|
| ADMIN | Administración general, usuarios, permisos, reportes y supervisión |
| CLIENTE | Consultar catálogo, registrar pedidos propios, registrar pagos, revisar sus diseños y consultar notificaciones propias |
| VENTAS | Mantener catálogo comercial, atender solicitudes y pedidos, consultar el resumen comercial |
| CAJA | Verificar pagos y consultar información necesaria para cobros |
| DISENO | Gestionar versiones de diseño y atender el flujo de revisión |
| PRODUCCION | Gestionar órdenes de trabajo, avances y control de calidad |
| ALMACEN | Administrar insumos, movimientos y existencias |

## Reglas de autorización

1. El registro público crea exclusivamente usuarios CLIENTE; los roles internos se asignan desde la administración de usuarios.
2. ADMIN puede acceder a todos los módulos internos.
3. VENTAS administra el catálogo comercial y las operaciones de pedidos autorizadas.
4. CAJA verifica pagos; registrar una constancia por parte del cliente no equivale a verificarla.
5. DISENO administra el trabajo de diseño. La aprobación de una versión corresponde al cliente propietario.
6. PRODUCCION gestiona órdenes y control de calidad.
7. ALMACEN gestiona insumos y movimientos de inventario.
8. Los guards de Angular mejoran la navegación, pero cada endpoint protegido debe validar también el rol en el backend.
9. La autorización por rol no reemplaza la comprobación de propiedad: un cliente solo puede consultar o decidir sobre recursos propios.
10. Las funciones todavía no implementadas, como proformas PDF y reservas de stock por pedido, siguen pendientes hasta que pasen sus pruebas de aceptación.

## Estado de implementación

La rama feature/roles-operativos-informe incorpora los siete roles al inicializador de datos, los contratos de tipos del frontend, la selección de roles en la administración de usuarios, guards de rutas y una primera asignación de permisos en controladores. Antes de integrar a main, deben ejecutarse las pruebas y revisarse los permisos de cada endpoint y las pantallas por rol.
