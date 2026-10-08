# api-gateway

Punto de entrada HTTP de SIGIP.

Puerto: `8090`.

Rutas iniciales:

- `/auth-service/**` → `auth-service:8093`
- `/catalog-service/**` → `catalog-service:8094`

La primera versión replica la forma de enrutamiento utilizada en las prácticas. Cuando la comunicación mediante Eureka quede validada, las rutas podrán evolucionar a descubrimiento dinámico.
