# auth-service

Autenticación y usuarios: login REST con usuario en BD, BCrypt y JWT.

## Funcionalidad

- `POST /api/v1/auth/login` → token JWT (público).
- `POST /api/v1/auth/registro` → crea un CLIENTE (público).
- `GET /api/v1/auth/me` → datos del usuario del token.
- `GET|POST|PUT|DELETE /api/v1/usuarios` → CRUD de usuarios (ADMIN).
- Al arrancar crea el administrador con `SIGIP_ADMIN_USERNAME` / `SIGIP_ADMIN_PASSWORD`.

## Pruebas

- `UsuarioRepositoryTest` (insertar, actualizar, eliminar, listar)
- `AuthControllerIntegrationTest` (login correcto, clave incorrecta, usuario inactivo, token, roles)
- `UsuarioServiceTest`

## Ejecución

Puerto `8093`. Base local `sigip_auth` (MySQL); en la nube, esquema propio en PostgreSQL.

Variables: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (o `DB_URL` completa).

```bash
mvn -pl backend/auth-service -am spring-boot:run
```
