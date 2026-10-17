# Organiza el proyecto: elimina los archivos de la estructura anterior que fueron reemplazados
# y activa el pipeline de GitHub Actions.
# Ejecutar UNA vez desde la carpeta del proyecto:
#   powershell -ExecutionPolicy Bypass -File scripts\limpiar-estructura.ps1
# (o doble clic en scripts\limpiar-estructura.bat)
#
# - Las carpetas design-service, inventory-service, notification-service y production-service de la raíz
#   se movieron a backend/.
# - El workflow de CI se copia a .github\workflows (esa carpeta no se puede escribir de forma remota).
# - Seguridad JWT y manejo de errores ahora viven en backend/sigip-common; las copias de cada servicio sobran.

$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot
Set-Location $raiz
Write-Host "Proyecto: $raiz"

$carpetas = @("design-service", "inventory-service", "notification-service", "production-service")
foreach ($c in $carpetas) {
    if (Test-Path $c) { Remove-Item $c -Recurse -Force; Write-Host "Eliminada carpeta $c" }
}

$archivos = @(
    "backend/auth-service/src/main/java/pe/edu/cibertec/sigip/authservice/exception/GlobalExceptionHandler.java",
    "backend/auth-service/src/main/java/pe/edu/cibertec/sigip/authservice/security/FiltroJwtAuth.java",
    "backend/auth-service/src/main/java/pe/edu/cibertec/sigip/authservice/security/JwtService.java",
    "backend/catalog-service/src/main/java/pe/edu/cibertec/sigip/catalogservice/exception/GlobalExceptionHandler.java",
    "backend/catalog-service/src/main/java/pe/edu/cibertec/sigip/catalogservice/security/FiltroJwtAuth.java",
    "backend/catalog-service/src/main/java/pe/edu/cibertec/sigip/catalogservice/security/JwtService.java",
    "backend/catalog-service/src/main/java/pe/edu/cibertec/sigip/catalogservice/security/SecurityConfig.java",
    "backend/report-service/src/main/java/pe/edu/cibertec/sigip/reportservice/security/FiltroJwtAuth.java",
    "backend/report-service/src/main/java/pe/edu/cibertec/sigip/reportservice/security/JwtService.java",
    "backend/report-service/src/main/java/pe/edu/cibertec/sigip/reportservice/security/SecurityConfig.java",
    "backend/sales-service/src/main/java/pe/edu/cibertec/sigip/salesservice/security/FiltroJwtAuth.java",
    "backend/sales-service/src/main/java/pe/edu/cibertec/sigip/salesservice/security/JwtService.java",
    "backend/sales-service/src/main/java/pe/edu/cibertec/sigip/salesservice/security/SecurityConfig.java"
)
foreach ($a in $archivos) {
    if (Test-Path $a) { Remove-Item $a -Force; Write-Host "Eliminado $a" }
}

# Borra las carpetas security/ y exception/ que quedaron vacías
Get-ChildItem backend -Recurse -Directory |
    Where-Object { $_.Name -in @("security", "exception") -and -not (Get-ChildItem $_.FullName -Recurse -File) } |
    Sort-Object FullName -Descending |
    ForEach-Object { Remove-Item $_.FullName -Recurse -Force; Write-Host "Eliminada carpeta vacía $($_.FullName)" }

# Activa el pipeline de GitHub Actions: copia el workflow a .github\workflows\ci.yml
if (-not (Test-Path ".github\workflows\ci.yml")) {
    New-Item -ItemType Directory -Force ".github\workflows" | Out-Null
    Copy-Item "scripts\github-actions\ci.yml" ".github\workflows\ci.yml"
    Write-Host "Creado .github\workflows\ci.yml"
}

Write-Host ""
Write-Host "Listo. Ahora puedes compilar con:  mvn clean verify" -ForegroundColor Green
