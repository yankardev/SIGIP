# SIGIP: pruebas manuales para la exposición

Todas las peticiones pasan por el **API Gateway**: `http://localhost:8090`
(en la nube, cambia esa parte por la URL de Render).

**Cómo usar el token en Postman:** después del login copia el valor de `token`, ve a la pestaña
**Authorization**, elige **Bearer Token** y pégalo. En las peticiones con cuerpo: **Body > raw > JSON**.

- 🔑 **ADMIN** = token del login de `admin`.
- 👤 **CLIENTE** = token del login del cliente (paso 1.3).
- Los `{id}` se reemplazan por el número que devolvió la petición anterior.
- Las respuestas son reales (ejecución del 13/10/2026). Los ids, fechas y tokens cambian en cada ejecución.

---

## 1. Login y usuarios (auth-service): BCrypt + JWT

### 1.1 Login del administrador (sin token)
```
POST http://localhost:8090/auth-service/api/v1/auth/login
```
```json
{ "nomusuario": "admin", "password": "Admin12345" }
```
**Debe mostrar: 200 OK**
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9...", "tipo": "Bearer",
  "usuario": { "idusuario": 1, "nomusuario": "admin", "roles": ["ADMIN"] } }
```

### 1.2 Registrar un cliente (sin token)
```
POST http://localhost:8090/auth-service/api/v1/auth/registro
```
```json
{ "nomusuario": "ana01", "email": "ana01@correo.pe", "password": "Cliente123",
  "nombres": "Ana", "apellidos": "Torres" }
```
**Debe mostrar: 201 Created**, con `"roles": ["CLIENTE"]`. Si lo repites, cambia `ana01` por otro nombre.

### 1.3 Login del cliente (sin token)
```
POST http://localhost:8090/auth-service/api/v1/auth/login
```
```json
{ "nomusuario": "ana01", "password": "Cliente123" }
```
**Debe mostrar: 200 OK** con su `token`. Guárdalo: es el token 👤 CLIENTE.

### 1.4 Ver mi perfil · 👤 CLIENTE
```
GET http://localhost:8090/auth-service/api/v1/auth/me
```
**Debe mostrar: 200 OK**, con los datos de `ana01`.

### 1.5 CRUD de usuarios · 🔑 ADMIN

| Acción | Método y URL | Body | Debe mostrar |
|---|---|---|---|
| Listar | `GET http://localhost:8090/auth-service/api/v1/usuarios` | — | 200, lista de usuarios |
| Crear | `POST http://localhost:8090/auth-service/api/v1/usuarios` | ver abajo | 201, `idusuario` nuevo |
| Actualizar | `PUT http://localhost:8090/auth-service/api/v1/usuarios/{id}` | ver abajo | 200, `"nombres": "Luis Alberto"` |
| Eliminar | `DELETE http://localhost:8090/auth-service/api/v1/usuarios/{id}` | — | **204 No Content** (sin cuerpo) |

Crear:
```json
{ "nomusuario": "caja01", "email": "caja01@sigip.local", "password": "Caja12345",
  "nombres": "Luis", "apellidos": "Ramos", "activo": true, "roles": ["ADMIN"] }
```
Actualizar:
```json
{ "nomusuario": "caja01", "email": "caja01@sigip.local",
  "nombres": "Luis Alberto", "apellidos": "Ramos", "activo": true, "roles": ["ADMIN"] }
```

**Mostrar que la contraseña está cifrada:** en MySQL ejecuta
`SELECT nomusuario, password FROM sigip_auth.usuario;`. La clave aparece como `$2a$10$...`.

---

## 2. Catálogo (catalog-service): GET, POST, PUT

### 2.1 Servicios activos (público, sin token)
```
GET http://localhost:8090/catalog-service/api/v1/servicios/activos
```
**Debe mostrar: 200 OK**, la lista del catálogo (Banner 1 x 2 m 70.00, Gigantografía m2 28.00, Packaging 4.50...).
Si la llamas dos veces, la segunda sale de la **caché** y responde más rápido.

### 2.2 Crear categoría · 🔑 ADMIN
```
POST http://localhost:8090/catalog-service/api/v1/categorias
```
```json
{ "nombre": "Señalética", "descripcion": "Letreros y avisos" }
```
**Debe mostrar: 201 Created** → `{"idcategoria": 6, "nombre": "Señalética", "activo": true}`.
Si responde **409** "Ya existe una categoría con ese nombre", cambia el nombre (esa también es una validación que puedes mostrar).

### 2.3 Crear servicio · 🔑 ADMIN (usa el `idcategoria` del paso anterior)
```
POST http://localhost:8090/catalog-service/api/v1/servicios
```
```json
{ "nombre": "Letrero acrílico 40x60", "descripcion": "Acrílico 3 mm con vinil",
  "precioBase": 85.0, "requiereCotizacion": false, "idcategoria": 6, "activo": true }
```
**Debe mostrar: 201 Created** → `"idservicio": 9, "precioBase": 85.0, "categoria": "Señalética"`.

### 2.4 Obtener y actualizar el servicio
```
GET http://localhost:8090/catalog-service/api/v1/servicios/{idservicio}
PUT http://localhost:8090/catalog-service/api/v1/servicios/{idservicio}    (🔑 ADMIN)
```
PUT con el mismo body del 2.3 pero `"precioBase": 90.0`.
**Debe mostrar: 200 OK** y el GET siguiente ya devuelve `90.00`.

---

## 3. Pedidos y pagos (sales-service): llama a catalog con Feign

### 3.1 Crear pedido · 👤 CLIENTE
```
POST http://localhost:8090/sales-service/api/v1/pedidos
```
```json
{ "idservicio": 9, "cantidad": 2, "especificaciones": "Logo en esquina superior" }
```
**Debe mostrar: 201 Created**. El precio lo trae sales desde catalog:
```json
{ "idpedido": 3, "cliente": "ana01", "nombreServicio": "Letrero acrílico 40x60",
  "precioUnitario": 90.00, "cantidad": 2, "total": 180.00, "estado": "REGISTRADO",
  "siguientesEstados": ["PAGO_EN_REVISION", "CANCELADO"] }
```

### 3.2 Mis pedidos y editar · 👤 CLIENTE
```
GET http://localhost:8090/sales-service/api/v1/pedidos
PUT http://localhost:8090/sales-service/api/v1/pedidos/{idpedido}
```
```json
{ "idservicio": 9, "cantidad": 3, "especificaciones": "Logo centrado" }
```
**Debe mostrar: 200 OK** → `"cantidad": 3, "total": 270.00`.

### 3.3 Registrar pago · 👤 CLIENTE
```
POST http://localhost:8090/sales-service/api/v1/pedidos/{idpedido}/pagos
```
```json
{ "monto": 270.0, "metodo": "YAPE", "numeroOperacion": "OP-123456" }
```
**Debe mostrar: 201 Created** → `"idpago": 3, "estado": "PENDIENTE"`.

### 3.4 Verificar pago · 🔑 ADMIN
```
POST http://localhost:8090/sales-service/api/v1/pagos/{idpago}/verificacion
```
```json
{ "aprobado": true, "observacion": "Yape conforme" }
```
**Debe mostrar: 200 OK** → `"estado": "VERIFICADO", "verificadoPor": "admin"`.

### 3.5 Resumen de ventas · 🔑 ADMIN
```
GET http://localhost:8090/sales-service/api/v1/pedidos/resumen
```
**Debe mostrar: 200 OK** → `totalPedidos`, `pedidosPorEstado` y `ventasConfirmadas`.

---

## 4. Diseño (design-service)

| Paso | Método y URL | Token | Body | Debe mostrar |
|---|---|---|---|---|
| Subir versión | `POST http://localhost:8090/design-service/api/v1/disenos` | 🔑 | ver abajo | 201, `"version": 1, "estado": "EN_REVISION"` |
| Ver diseños | `GET http://localhost:8090/design-service/api/v1/disenos` | 👤 | — | 200, solo los diseños de ese cliente |
| Aprobar | `POST http://localhost:8090/design-service/api/v1/disenos/{iddiseno}/decision` | 👤 | ver abajo | 200, `"estado": "APROBADO"` |

```json
{ "idpedido": 3, "nombreArchivo": "letrero-v1.pdf",
  "urlArchivo": "https://drive.google.com/file/d/ejemplo", "descripcion": "Primera propuesta" }
```
```json
{ "decision": "APROBAR", "comentario": "Conforme" }
```

---

## 5. Producción (production-service) · 🔑 ADMIN

| Paso | Método y URL | Body | Debe mostrar |
|---|---|---|---|
| Crear orden | `POST http://localhost:8090/production-service/api/v1/ordenes-produccion` | `{ "idpedido": 3, "prioridad": "ALTA", "responsable": "Taller 1" }` | 201, `"estado": "PENDIENTE"`, `"descripcionTrabajo": "3 x Letrero acrílico 40x60"` |
| Cola | `GET http://localhost:8090/production-service/api/v1/ordenes-produccion` | — | 200, órdenes ordenadas por prioridad |
| Iniciar | `POST http://localhost:8090/production-service/api/v1/ordenes-produccion/{idorden}/inicio` | — | 200, `"estado": "EN_PROCESO"` |
| Calidad | `POST http://localhost:8090/production-service/api/v1/ordenes-produccion/{idorden}/calidad` | `{ "aprobado": true, "observacion": "Sin defectos" }` | 200, `"estado": "TERMINADA"` |

Después de la calidad, el pedido del cliente pasa a **TERMINADO** (GET de "Mis pedidos").

---

## 6. Inventario (inventory-service) · 🔑 ADMIN

| Paso | Método y URL | Body | Debe mostrar |
|---|---|---|---|
| Crear insumo | `POST http://localhost:8090/inventory-service/api/v1/insumos` | ver abajo | 201, `"stockActual": 10, "bajoStock": false` |
| Salida de stock | `POST http://localhost:8090/inventory-service/api/v1/insumos/{idinsumo}/movimientos` | `{ "tipo": "SALIDA", "cantidad": 7, "motivo": "Pedido", "idpedido": 3 }` | 201, `"stockResultante": 3.000` |
| Bajo stock | `GET http://localhost:8090/inventory-service/api/v1/insumos/bajo-stock` | — | 200, aparece el insumo con `"bajoStock": true` |
| Actualizar | `PUT http://localhost:8090/inventory-service/api/v1/insumos/{idinsumo}` | igual al de crear, con `"stockMinimo": 5` | 200 |

```json
{ "codigo": "ACR-01", "nombre": "Plancha acrílico 3 mm", "unidadMedida": "PLANCHA",
  "stockActual": 10, "stockMinimo": 4, "activo": true }
```

---

## 7. Notificaciones (notification-service): eventos por RabbitMQ

| Paso | Método y URL | Token | Debe mostrar |
|---|---|---|---|
| Mis avisos | `GET http://localhost:8090/notification-service/api/v1/notificaciones` | 👤 | 200. Con RabbitMQ (Docker), avisos automáticos de cada cambio de estado del pedido |
| Crear aviso | `POST http://localhost:8090/notification-service/api/v1/notificaciones` | 🔑 | 201, `"estado": "PENDIENTE"` |
| Marcar leído | `PATCH http://localhost:8090/notification-service/api/v1/notificaciones/{id}/leida` | 👤 | 200, `"estado": "LEIDA"` |
| Eliminar | `DELETE http://localhost:8090/notification-service/api/v1/notificaciones/{id}` | 🔑 | **204 No Content** |

```json
{ "destinatario": "ana01", "canal": "EMAIL", "asunto": "Recordatorio",
  "mensaje": "Tu pedido está listo", "idpedido": 3 }
```

---

## 8. Reportes (report-service): consolida ventas + inventario

```
GET http://localhost:8090/report-service/api/v1/reportes/dashboard
```
- Con 🔑 ADMIN, **debe mostrar 200 OK**:
```json
{ "ventas": { "totalPedidos": 3, "ventasConfirmadas": 810.00, "pedidosPorEstado": { "...": 0 } },
  "pedidosPorAtender": 0, "pedidosEnTaller": 0,
  "insumosBajoStock": [ { "codigo": "ACR-01", "stockActual": 3.000, "stockMinimo": 5.000 } ],
  "avisos": ["1 insumo(s) con stock bajo el mínimo"] }
```
- Con 👤 CLIENTE, **debe mostrar 403 Forbidden**. Así se demuestra que la seguridad por roles funciona:
```json
{ "status": 403, "error": "FORBIDDEN", "message": "No tiene permisos para realizar esta operación" }
```

---

## 9. Pruebas de errores (buenas para mostrar seguridad y validaciones)

| Prueba | Petición | Debe mostrar |
|---|---|---|
| Contraseña incorrecta | `POST .../auth-service/api/v1/auth/login` con `{ "nomusuario": "admin", "password": "mala" }` | **401** `"error": "INVALID_CREDENTIALS"`, `"Usuario o contraseña incorrectos"` |
| Sin token | `GET http://localhost:8090/sales-service/api/v1/pedidos` con Authorization en *No Auth* | **401** `"Debe iniciar sesión con un token válido"` |
| Datos inválidos (🔑) | `POST .../catalog-service/api/v1/servicios` con `{ "nombre": "", "precioBase": -5 }` | **400** `"error": "VALIDATION_ERROR"`, `"El precio base no puede ser negativo"` |
| No existe | `GET http://localhost:8090/catalog-service/api/v1/servicios/9999` | **404** `"Servicio de impresión no encontrado"` |

---

## 10. Monitoreo (navegador)

| URL | Debe mostrar |
|---|---|
| http://localhost:8761 | Eureka con los 8 servicios y API-GATEWAY en estado UP |
| http://localhost:8090/actuator/health | `"status": "UP"` |
| http://localhost:15672 | Panel de RabbitMQ, con la cola `sigip.notificaciones` (usuario y clave del `.env`) |

**Consejo para la exposición:** lo más rápido es Postman > *Run collection* (36 peticiones en ~12 s).
Este documento sirve para cuando te pidan hacer una prueba concreta a mano.

---

## 11. Demo desde el frontend Angular (http://localhost:4200)

Usa dos navegadores (o una ventana normal y otra de incógnito): uno para el **cliente** y otro para el **admin**.

| Paso | Quién | Qué hacer en pantalla | Qué debe mostrar |
|---|---|---|---|
| 1 | Cliente | Login > "Crea tu cuenta", completa el formulario | Entra directo al **Catálogo** con los servicios activos |
| 2 | Cliente | "Solicitar" en un servicio, cantidad 500, especificaciones | Toast "Pedido #N registrado" y la ficha del pedido en **Registrado** |
| 3 | Cliente | "Registrar pago" (Yape, n.º de operación) | Pedido pasa a **Pago en revisión** |
| 4 | Admin | Login `admin` / `Admin12345` | Abre el **Dashboard** con ventas, pedidos por estado e insumos críticos |
| 5 | Admin | Pedidos > Ver > "Verificar" > Aprobar | Pedido pasa a **Confirmado** (venta confirmada en el dashboard) |
| 6 | Admin | "Subir diseño" > `tarjetas-v1.pdf` | Diseño v1 **En revisión** |
| 7 | Cliente | Diseños > "Observar" con un comentario | v1 **Observado** |
| 8 | Admin | Diseños > "Nueva versión" `tarjetas-v2.pdf` | v1 **Reemplazado**, v2 **En revisión** |
| 9 | Cliente | Diseños > "Aprobar" | v2 **Aprobado** |
| 10 | Admin | Pedido > "Crear orden de producción" (prioridad Alta) > Iniciar | Pedido pasa a **En producción** (production llama a sales con Feign) |
| 11 | Admin | Producción > "Control de calidad" > Aprobado | Orden **Terminada** y pedido **Terminado** |
| 12 | Admin | Pedido > "Cambiar estado" > Entregado | Barra de avance completa |
| 13 | Admin | Inventario > Nuevo insumo (stock 100, mínimo 50) > "− Salida" de 60 > Kárdex | Insumo marcado **Reponer** y aparece en el dashboard |
| 14 | Cliente | Campana ✉ / Notificaciones | Avisos de cada cambio de estado, llegados por **RabbitMQ** |
| 15 | Cliente | Escribe `/inventario` en la barra de direcciones | El guard lo devuelve al catálogo (y el backend respondería 403) |

En las herramientas del navegador (F12 > Red) se ve que cada petición va al API Gateway con el encabezado
`Authorization: Bearer <token>`.
