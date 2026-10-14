# SIGIP: cómo levantar el proyecto y subirlo a GitHub

## 0. La limpieza ya está hecha

Revisé tu carpeta y `limpiar-estructura.bat` funcionó: ya no están las carpetas viejas de la raíz ni las
copias de seguridad JWT de cada servicio, y `.github/workflows/ci.yml` está en su lugar.
No hay que volver a ejecutarlo. (Se ejecuta con doble clic en el Explorador de Windows o desde la
terminal de IntelliJ; las dos formas funcionan.)

---

## 1. Levantar el proyecto

Hay dos caminos. **Recomiendo la opción A** porque no necesitas instalar Java 25, MySQL ni RabbitMQ.

### Opción A (recomendada): Docker Desktop, todo con un comando

1. Instala Docker Desktop: https://www.docker.com/products/docker-desktop/ (reinicia si te lo pide).
2. Abre la carpeta `SIGIP-develop` en IntelliJ y abre la terminal (`Alt+F12`).
3. Crea tu archivo `.env` a partir del ejemplo:
   ```powershell
   copy .env.example .env
   ```
   Abre `.env` y cambia `JWT_SECRET` por una clave larga. Puedes generarla en PowerShell:
   ```powershell
   [Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
   ```
4. Levanta todo (la primera vez tarda porque compila los 10 servicios y el frontend dentro de Docker):
   ```powershell
   docker compose up -d --build
   ```
5. Revisa que todo esté arriba:
   ```powershell
   docker compose ps
   ```
   Todos deben decir `running` o `healthy`. Si alguno falla: `docker compose logs nombre-del-servicio`.
   Si dice que el puerto 3306 está ocupado, es porque tienes MySQL instalado y encendido en Windows:
   detén el servicio MySQL (Servicios de Windows) y vuelve a ejecutar el comando.
6. Abre en el navegador:
   - **Frontend Angular: http://localhost:4200** (entra con `admin` / `Admin12345` o crea un cliente en "Crea tu cuenta")
   - Eureka (deben aparecer los 8 servicios + gateway): http://localhost:8761
   - Salud del gateway: http://localhost:8090/actuator/health
   - RabbitMQ (usuario y clave del `.env`): http://localhost:15672

Para apagar todo: `docker compose down` (con `-v` también borra los datos).

### Opción B: desde IntelliJ, servicio por servicio

Requisitos:
- **JDK 25**: en IntelliJ `File > Project Structure > SDK > Download JDK > versión 25`.
- **MySQL 8** con usuario `root`.
- RabbitMQ es opcional (si no lo tienes, apaga los eventos como se indica abajo).

Pasos:
1. Crea las bases de datos (una sola vez), desde la terminal o con MySQL Workbench ejecutando el archivo:
   ```powershell
   mysql -u root -p < infra\mysql\init\01-create-databases.sql
   ```
2. Define las variables de entorno **una sola vez** en PowerShell y luego **reinicia IntelliJ** para que las tome:
   ```powershell
   setx DB_USER "root"
   setx DB_PASSWORD "tu_clave_de_mysql"
   setx JWT_SECRET "pega-aqui-la-clave-generada-en-el-paso-3-de-la-opcion-A"
   setx SIGIP_ADMIN_PASSWORD "Admin12345"
   setx EVENTOS_HABILITADOS "false"
   ```
   (Si instalaste RabbitMQ, no pongas `EVENTOS_HABILITADOS`.)
3. Compila y corre las 78 pruebas: en IntelliJ abre el panel **Maven** (derecha) >
   `sigip-parent > Lifecycle > clean`, luego `install`. Debe terminar en **BUILD SUCCESS**.
4. Arranca cada servicio con el botón ▶ de su clase `...Application.java`, **en este orden**:
   1. `discovery-server` (DiscoveryServerApplication)
   2. `auth-service`
   3. `catalog-service`
   4. `sales-service`
   5. `design-service`, `production-service`, `inventory-service`, `notification-service`, `report-service`
   6. `api-gateway` (al final)

   Consejo: en la ventana **Services** de IntelliJ (`Alt+8`) puedes ver y arrancar todos juntos.
5. Verifica en http://localhost:8761 que aparezcan todos registrados.
6. Frontend Angular (requiere **Node.js 22** o 24 desde https://nodejs.org). En una terminal nueva de IntelliJ:
   ```powershell
   cd frontend
   npm install
   npm start
   ```
   Abre http://localhost:4200. En este modo el navegador llama directo al gateway `http://localhost:8090`
   (el CORS del gateway ya permite `http://localhost:4200`).

---

## 2. Probar que todo funciona

### Con Postman (lo más rápido)

1. Postman > **Import** > elige `postman\SIGIP.postman_collection.json`.
2. En la colección, pestaña **Variables**: `gateway` = `http://localhost:8090` y
   `admin_password` = la clave que pusiste en `SIGIP_ADMIN_PASSWORD` (por defecto `Admin12345`).
3. Clic derecho en la colección > **Run collection** > **Run**.
   Resultado esperado: las 36 peticiones en verde. Una devuelve **403 a propósito**
   (un cliente intentando una acción de administrador) y también cuenta como correcta.

La colección recorre todo: login, CRUD de catálogo, pedido, pago, diseño, producción, inventario,
notificaciones y el dashboard de reportes.

### Prueba manual del login (para el video)

`POST http://localhost:8090/auth-service/api/v1/auth/login` con este cuerpo JSON:
```json
{ "nomusuario": "admin", "password": "Admin12345" }
```
Devuelve un `token`. Úsalo en las demás peticiones con el encabezado
`Authorization: Bearer <token>`.

En MySQL puedes mostrar que la contraseña está cifrada con BCrypt:
```sql
SELECT nomusuario, password FROM sigip_auth.usuario;
```
(La clave aparece como `$2a$10$...`, nunca en texto plano.)

---

## 3. Subir todo al GitHub de tu compañero

Tu carpeta `SIGIP-develop` vino de un ZIP, así que **no tiene Git**. Lo más seguro es clonar el
repositorio en otra carpeta, copiar encima tus archivos y subirlos en una **rama nueva con un Pull Request**.
Así tu compañero ve exactamente qué cambió antes de unirlo a `develop`, y si él subió algo
después de que descargaste el ZIP, no se pierde sin que nadie lo note.

Necesitas Git instalado (https://git-scm.com/download/win) y haber iniciado sesión en GitHub con tu
cuenta (que ya tiene permiso de escritura en el repositorio).

### Paso a paso (PowerShell)

1. Configura tu nombre (una sola vez):
   ```powershell
   git config --global user.name "Tu Nombre"
   git config --global user.email "tu-correo@ejemplo.com"
   ```
2. Clona el repositorio en una carpeta nueva y entra a `develop`:
   ```powershell
   cd $HOME\Downloads
   git clone https://github.com/yankardev/SIGIP.git SIGIP-git
   cd SIGIP-git
   git checkout develop
   git pull
   ```
3. Crea tu rama de trabajo:
   ```powershell
   git checkout -b feature/proyecto-completo
   ```
4. Copia tu versión encima del clon. `/MIR` deja el clon igual a tu carpeta: agrega lo nuevo,
   reemplaza lo cambiado y **borra los archivos viejos** que eliminó la limpieza.
   Se excluyen `.git`, las carpetas compiladas y tu `.env` con claves:
   ```powershell
   robocopy $HOME\Downloads\SIGIP-develop . /MIR /XD .git target .idea node_modules /XF .env *.iml
   ```
5. **Revisa antes de confirmar**:
   ```powershell
   git status
   ```
   - `modified` y `new file` son normales.
   - `deleted` debe ser solo lo viejo (las carpetas `design-service`, `inventory-service`,
     `notification-service`, `production-service` de la raíz y los `security/` y `exception/`
     duplicados). Si ves borrado algún archivo que tu compañero agregó hace poco, recupéralo con
     `git checkout -- ruta\del\archivo` antes de seguir.
   - Confirma que **no** aparezca `.env`.
6. Compila una vez más en el clon para estar seguro (opcional si ya probaste en la opción A/B):
   ```powershell
   mvn clean verify
   ```
7. Guarda el cambio y súbelo:
   ```powershell
   git add -A
   git commit -m "Completa SIGIP: microservicios, pruebas, CI/CD, optimización y despliegue en la nube"
   git push -u origin feature/proyecto-completo
   ```
   La primera vez se abrirá una ventana para iniciar sesión en GitHub.
8. Crea el Pull Request: entra a https://github.com/yankardev/SIGIP, aparecerá el botón
   **Compare & pull request**. Elige **base: develop** ← **compare: feature/proyecto-completo**,
   escribe una descripción corta y clic en **Create pull request**.
9. Espera que el check de **GitHub Actions (CI)** salga en verde (compila y corre las 78 pruebas) y
   luego **Merge pull request**. Con eso `develop` queda con todo el proyecto.

Si después quieres que `main` también lo tenga, crea otro Pull Request de `develop` a `main`.

### Si algo falla

| Problema | Solución |
|---|---|
| `git push` dice "permission denied" o 403 | Tu cuenta aún no tiene permiso: pídele a tu compañero que te agregue en *Settings > Collaborators*. |
| `git push` rechazado porque la rama ya existe | Usa otro nombre: `git checkout -b feature/proyecto-completo-2`. |
| El CI falla en GitHub | Abre la pestaña *Actions*, entra al paso en rojo y copia el error; te ayudo a corregirlo. |
| `robocopy` muestra un código 1 a 3 al terminar | Es normal: significa que copió archivos. Solo 8 o más es error. |
