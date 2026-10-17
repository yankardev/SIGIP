# Subir SIGIP a GitHub desde IntelliJ IDEA

Se trabaja en la misma carpeta `C:\Users\Pedro\Downloads\SIGIP-develop`. Git compara tus archivos con
la rama `develop` del repositorio y sube solo las diferencias en una rama nueva.

## 1. Conectar la carpeta con el repositorio

Todo se hace en la terminal de IntelliJ (`Alt+F12`), dentro de `SIGIP-develop`.

```powershell
git init
git remote add origin https://github.com/yankardev/SIGIP.git
git fetch origin
git reset origin/develop
git checkout -b feature/proyecto-completo
```

`git reset origin/develop` no borra ni cambia ningún archivo tuyo. Solo le dice a Git que tu punto de
partida es el `develop` de GitHub, para que vea qué agregaste, cambiaste y eliminaste.

Comprueba que el `.env` con tu clave NO se va a subir:
```powershell
git check-ignore .env
```
Debe responder `.env`. Si no responde nada, detente y avísame.

## 2. Commit y push (todo en la terminal)

```powershell
git config --global user.name "Tu Nombre"
git config --global user.email "tu-correo@ejemplo.com"
git add -A
git status --short | Select-String "\.env|target/|\.idea"
```
El último comando no debe mostrar **nada** (si muestra `.env`, `target` o `.idea`, detente y avísame).

Puedes ver el resumen de lo que se va a subir (`A` nuevo, `M` modificado, `D` eliminado):
```powershell
git status --short
```
Las `D` son las carpetas viejas de la raíz (`design-service`, `inventory-service`, `notification-service`,
`production-service`) y los archivos de seguridad duplicados: es correcto.

```powershell
git commit -m "Completa SIGIP: microservicios, pruebas, CI/CD, optimizacion y despliegue en la nube"
git push -u origin feature/proyecto-completo
```
La primera vez se abre una ventana para iniciar sesión en GitHub: elige **Sign in with your browser**.

## 3. Pull Request

Al terminar el push, la terminal imprime un enlace como este (también puedes abrirlo directo):

https://github.com/yankardev/SIGIP/pull/new/feature/proyecto-completo

1. Ábrelo (Ctrl+clic en la terminal de IntelliJ), revisa **base: develop** ← **compare: feature/proyecto-completo**
   y pulsa **Create pull request**.
2. Espera a que el check **CI** salga en verde y pulsa **Merge pull request** > **Confirm merge**.
3. Para dejar tu carpeta al día después del merge:
```powershell
git checkout develop
git pull origin develop
```

## Si algo falla

| Mensaje | Qué hacer |
|---|---|
| `fatal: remote origin already exists` | Ya estaba conectado; sigue con `git fetch origin`. |
| `Permission denied` o `403` al hacer push | Tu cuenta de GitHub no es colaborador: tu compañero debe agregarte en *Settings > Collaborators*. |
| `Author identity unknown` | `git config --global user.name "Tu Nombre"` y `git config --global user.email "tu@correo.com"`, y repite el commit. |
| El check CI sale en rojo | Entra al check, copia el error y pégamelo. |
