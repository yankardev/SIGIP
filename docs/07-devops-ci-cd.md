# DevOps: Git, integración continua, Maven y Docker

## 1. Control de versiones con Git

- Repositorio: https://github.com/yankardev/SIGIP
- Ramas: `main` (versión estable que se presenta) y `develop` (integración del equipo). Cada cambio
  grande se trabaja en una rama `feature/...` y entra a `develop` por pull request.
- `.gitignore` excluye `target/`, `.env` y archivos con claves; los secretos llegan por variables de entorno.

## 2. Build automatizado con Maven

El `pom.xml` de la raíz es un **POM padre multi-módulo**: centraliza versiones (Java 25, Spring Boot 4.1.1,
Spring Cloud 2025.1.3, JJWT) y plugins, y compila los 11 módulos en el orden correcto con un solo comando:

```bash
mvn clean verify
```

| Fase | Qué ocurre |
|---|---|
| compile | Compila `sigip-common` y luego cada microservicio (Lombok como procesador de anotaciones) |
| test | 78 pruebas JUnit 5: repositorios (`@DataJpaTest` + H2), REST (`MockMvc`), seguridad y reglas |
| verify | JaCoCo genera la cobertura en `target/site/jacoco/` |
| package | Un `.jar` ejecutable por servicio (`target/<servicio>.jar`) |

## 3. Pipeline CI con GitHub Actions

Archivo: `.github/workflows/ci.yml`. Se ejecuta en cada push o pull request a `main` y `develop`.

```
push / PR ─► build-test ─────────────────────────────► docker (10 imágenes en paralelo)
             ├ checkout                                ├ docker buildx
             ├ JDK 25 (Temurin) + caché de Maven       └ caché de capas (GitHub Actions cache)
             ├ mvn -B clean verify
             ├ resumen de la optimización en la página del run
             └ artefactos: surefire-reports, jacoco
```

- Si una prueba falla, el job queda en rojo y el pull request no se debería fusionar.
- `concurrency` cancela ejecuciones antiguas de la misma rama para no gastar minutos.
- La caché de dependencias Maven reduce el tiempo del build en las siguientes ejecuciones.

**Alternativa Jenkins:** el `Jenkinsfile` de la raíz define las mismas etapas (checkout, `mvn verify`,
publicación de JUnit/JaCoCo y construcción de imágenes en `main`).

## 4. Contenedores con Docker

Un único `Dockerfile` multi-etapa sirve para cualquier servicio:

```bash
docker build --build-arg SERVICE=sales-service -t sigip/sales-service .
```

1. Etapa `build` (maven + JDK 25): compila solo el módulo pedido y sus dependencias (`-pl ... -am`).
2. Etapa final (`eclipse-temurin:25-jre`): solo el JRE y el `.jar`, con usuario sin privilegios.

`docker-compose.yml` levanta el sistema completo: MySQL (con el script que crea las 7 bases), RabbitMQ,
Eureka, el gateway y los 8 servicios. Las claves se leen de `.env` (plantilla en `.env.example`).

```bash
cp .env.example .env
docker compose up -d --build
docker compose up -d --scale catalog-service=2   # escalado horizontal; el gateway reparte la carga
```

## 5. Entrega continua (CD)

El despliegue a la nube está descrito como código en `render.yaml`: al fusionar a `main`, Render
reconstruye las imágenes y publica la nueva versión automáticamente (autodeploy). Ver `09-despliegue-nube.md`.
