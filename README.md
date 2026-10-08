# SIGIP

**Sistema Integral de Gestión de Impresión y Pedidos**

Plataforma web empresarial para una imprenta: conecta el catálogo y la experiencia de compra/cotización del cliente con la gestión interna de ventas, proformas, diseños, pagos, materiales, producción, calidad y entrega.

## Enfoque del producto

SIGIP tendrá dos experiencias conectadas:

- **Portal del cliente (ecommerce):** catálogo, configuración de servicios, solicitudes de cotización, proformas, pedidos, archivos, aprobaciones y seguimiento.
- **Backoffice empresarial:** ventas, caja, diseño, almacén, producción, calidad, entregas, reportes, usuarios y permisos.

El flujo de negocio principal es:

```text
Cliente
  ↓
Catálogo / configuración
  ↓
Solicitud o compra
  ↓
Proforma
  ↓
Aceptación
  ↓
Pedido
  ↓
Pago / condición comercial
  ↓
Diseño y aprobación
  ↓
Reserva de materiales
  ↓
Producción
  ↓
Control de calidad
  ↓
Entrega
```

## Arquitectura prevista

```text
Angular
   ↓
API Gateway :8090
   ├── auth-service :8093
   └── catalog-service :8094
          ↑
      Eureka :8761
```

Servicios posteriores:

- sales-service: clientes, solicitudes, proformas, pedidos, pagos y entregas.
- production-service: archivos, versiones de diseño, aprobaciones, órdenes de trabajo y calidad.
- inventory-service: insumos, reservas, consumos y movimientos.
- notification-service: avisos y reintentos.

La comunicación síncrona se utilizará cuando la operación necesite una respuesta inmediata; la asincronía con RabbitMQ se reservará para eventos, notificaciones y procesos posteriores que no deben bloquear la decisión principal.

## Estado actual

### Implementado en develop

- auth-service: estructura Spring Boot, usuarios/roles, BCrypt, login JWT, filtro JWT, @PreAuthorize, validaciones y pruebas de repositorio.
- catalog-service: categorías, servicios de impresión, CRUD REST, persistencia, validaciones, pruebas de repositorio y protección JWT para operaciones administrativas.
- discovery-server: Eureka Server.
- api-gateway: Gateway inicial con rutas hacia autenticación y catálogo.
- Bases iniciales MySQL para autenticación y catálogo.

### Pendiente

- Validación local de ejecución con MySQL.
- Pruebas HTTP de login y CRUD.
- Registro comprobado de servicios en Eureka.
- Feign entre servicios.
- Portal Angular.
- Clientes, solicitudes, proformas y pedidos.
- Diseños y aprobaciones.
- Inventario y producción.
- RabbitMQ, outbox, deduplicación y reintentos.
- Resiliencia, Docker/Kubernetes y observabilidad.

No se considera una funcionalidad terminada solo por existir su carpeta o código; debe ejecutarse y contar con evidencia.

## Estructura

```text
SIGIP/
├── backend/
│   ├── discovery-server/
│   ├── api-gateway/
│   ├── auth-service/
│   └── catalog-service/
├── frontend/
├── infra/
├── docs/
└── README.md
```

## Configuración local

No se almacenan contraseñas ni secretos en Git.

Para auth-service:

```text
DB_USER=root
DB_PASSWORD=<tu-clave-local>
JWT_SECRET=<secreto-base64-de-al-menos-32-bytes>
JWT_EXPIRATION_MINUTES=30
SIGIP_ADMIN_PASSWORD=<clave-local-para-admin>
```

Crear primero las bases indicadas en:

infra/mysql/init/01-create-databases.sql

Los detalles de cada servicio se encuentran en su README.

## Ramas

- main: versión estable.
- develop: integración y desarrollo del proyecto.
