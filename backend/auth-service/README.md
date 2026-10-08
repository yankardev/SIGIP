# auth-service

Servicio de autenticación y autorización de SIGIP.

## Objetivo del primer incremento

Implementar el patrón trabajado en clase:

- usuarios y roles persistidos;
- `BCryptPasswordEncoder`;
- login REST;
- JWT;
- `SecurityFilterChain`;
- filtro JWT;
- autorización por roles con `@PreAuthorize`;
- validaciones y respuestas controladas.

## Configuración local

Antes de ejecutar, crear la base de datos:

```sql
CREATE DATABASE sigip_auth;
```

Definir las variables de entorno:

```text
DB_USER=root
DB_PASSWORD=<tu-clave-local>
JWT_SECRET=<secreto-base64-de-al-menos-32-bytes>
```

La aplicación usa el puerto `8093` en esta primera etapa. Más adelante será registrada en Eureka y consumida a través del API Gateway.

## Estado

La estructura Maven y la aplicación Spring Boot ya están creadas. Las entidades, repositorios y seguridad se implementan en el siguiente paso.
