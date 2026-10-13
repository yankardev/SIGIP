# discovery-server

Servidor Eureka: registro y descubrimiento de servicios.

## Funcionalidad

- Panel: http://localhost:8761
- Cada servicio se registra con un `instance-id` aleatorio, así se pueden levantar varias instancias del mismo servicio.
- En la nube (perfil `cloud`) no se usa: los servicios se ubican por variables de entorno.

## Ejecución

Puerto `8761`.

```bash
mvn -pl backend/discovery-server -am spring-boot:run
```
