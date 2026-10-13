# catalog-service

Catálogo: categorías y servicios de impresión, con caché Caffeine.

## Funcionalidad

- `GET /api/v1/servicios`, `/servicios/activos`, `/servicios/{id}` y `GET /api/v1/categorias` son públicos.
- POST, PUT y DELETE requieren ADMIN.
- No se elimina una categoría con servicios (409).
- Consultas con `JOIN FETCH` (sin N+1) y caché que se invalida al modificar.
- Datos de demostración al iniciar con la base vacía (`CATALOGO_DATOS_DEMO=false` para desactivar).

## Pruebas

- `CategoriaRepositoryTest`, `ServicioImpresionRepositoryTest`
- `CatalogoControllerIntegrationTest`
- `OptimizacionCatalogoTest`: mide N+1 vs JOIN FETCH vs caché y genera `target/optimizacion-catalogo.md`

## Ejecución

Puerto `8094`. Base local `sigip_catalog` (MySQL); en la nube, esquema propio en PostgreSQL.

Variables: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (o `DB_URL` completa).

```bash
mvn -pl backend/catalog-service -am spring-boot:run
```
