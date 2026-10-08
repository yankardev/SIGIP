# catalog-service

Servicio responsable del catálogo comercial de SIGIP.

## Primera versión

- Categorías.
- Servicios de impresión.
- Precio base.
- Indicador de cotización.
- CRUD REST.
- Persistencia MySQL.
- Pruebas de repositorio.

## Base de datos local

```sql
CREATE DATABASE sigip_catalog;
```

Variables de entorno:

```text
DB_USER=root
DB_PASSWORD=<tu-clave-local>
```

Puerto local: `8094`.

## Ejemplos de servicios

- Volantes.
- Tarjetas personales.
- Stickers.
- Banners.
- Gigantografías.
- Trabajos especiales que requieren cotización.

Las opciones técnicas detalladas, tarifas por cantidad y versiones de precio se incorporarán después; no se deben simular como si ya estuvieran implementadas.
