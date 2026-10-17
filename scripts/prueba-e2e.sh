#!/usr/bin/env bash
# Recorre el flujo completo de SIGIP a través del API Gateway (sirve para la demo del video).
# Uso:  ./scripts/prueba-e2e.sh [URL_GATEWAY] [CLAVE_ADMIN]
# Requiere curl y jq.
set -euo pipefail
G=${1:-http://localhost:8090}
ADMIN_PASS=${2:-Admin12345}
J='Content-Type: application/json'
paso() { printf '\n\033[1m== %s\033[0m\n' "$1"; }

paso "1. Login del administrador (usuario en BD, contraseña con BCrypt)"
ADMIN=$(curl -sf -X POST "$G/auth-service/api/v1/auth/login" -H "$J" \
  -d "{\"nomusuario\":\"admin\",\"password\":\"$ADMIN_PASS\"}" | jq -r .token)
echo "token: ${ADMIN:0:25}..."

paso "2. Registro y login de un cliente"
CLI_USER="cliente$RANDOM"
curl -sf -X POST "$G/auth-service/api/v1/auth/registro" -H "$J" -d "{\"nomusuario\":\"$CLI_USER\",\"email\":\"$CLI_USER@correo.pe\",\"password\":\"Cliente123\",\"nombres\":\"Ana\",\"apellidos\":\"Torres\"}" | jq -c '{idusuario,nomusuario,roles}'
CLI=$(curl -sf -X POST "$G/auth-service/api/v1/auth/login" -H "$J" \
  -d "{\"nomusuario\":\"$CLI_USER\",\"password\":\"Cliente123\"}" | jq -r .token)

paso "3. Catálogo público (con caché)"
SERV=$(curl -sf "$G/catalog-service/api/v1/servicios/activos")
echo "$SERV" | jq -c '.[0:3][] | {idservicio,nombre,precioBase}'
ID_SERV=$(echo "$SERV" | jq '.[0].idservicio')

paso "4. El cliente registra un pedido (precio tomado del catálogo vía Feign)"
PED=$(curl -sf -X POST "$G/sales-service/api/v1/pedidos" -H "$J" -H "Authorization: Bearer $CLI" \
  -d "{\"idservicio\":$ID_SERV,\"cantidad\":100,\"especificaciones\":\"Full color, papel couché\"}")
echo "$PED" | jq -c '{idpedido,nombreServicio,total,estado,siguientesEstados}'
ID_PED=$(echo "$PED" | jq .idpedido); TOTAL=$(echo "$PED" | jq .total)

paso "5. Pago por Yape y verificación del administrador"
PAGO=$(curl -sf -X POST "$G/sales-service/api/v1/pedidos/$ID_PED/pagos" -H "$J" -H "Authorization: Bearer $CLI" \
  -d "{\"monto\":$TOTAL,\"metodo\":\"YAPE\",\"numeroOperacion\":\"OP-$RANDOM\"}")
ID_PAGO=$(echo "$PAGO" | jq .idpago)
curl -sf -X POST "$G/sales-service/api/v1/pagos/$ID_PAGO/verificacion" -H "$J" -H "Authorization: Bearer $ADMIN" \
  -d '{"aprobado":true}' | jq -c '{idpago,estado}'

paso "6. Orden de producción: inicio y control de calidad"
ORD=$(curl -sf -X POST "$G/production-service/api/v1/ordenes-produccion" -H "$J" -H "Authorization: Bearer $ADMIN" \
  -d "{\"idpedido\":$ID_PED,\"prioridad\":\"ALTA\",\"responsable\":\"Taller 1\"}")
ID_ORD=$(echo "$ORD" | jq .idorden)
curl -sf -X POST "$G/production-service/api/v1/ordenes-produccion/$ID_ORD/inicio" -H "Authorization: Bearer $ADMIN" | jq -c '{idorden,estado}'
curl -sf -X POST "$G/production-service/api/v1/ordenes-produccion/$ID_ORD/calidad" -H "$J" -H "Authorization: Bearer $ADMIN" \
  -d '{"aprobado":true}' | jq -c '{idorden,estado}'

paso "7. El cliente consulta su pedido"
curl -sf "$G/sales-service/api/v1/pedidos/$ID_PED" -H "Authorization: Bearer $CLI" | jq -c '{idpedido,estado,siguientesEstados}'

paso "8. Seguridad: un cliente no puede ver reportes (403) y sin token es 401"
curl -s -o /dev/null -w "cliente -> %{http_code}\n" "$G/report-service/api/v1/reportes/dashboard" -H "Authorization: Bearer $CLI"
curl -s -o /dev/null -w "sin token -> %{http_code}\n" "$G/sales-service/api/v1/pedidos"

paso "9. Dashboard del administrador (report-service consolida ventas e inventario)"
curl -sf "$G/report-service/api/v1/reportes/dashboard" -H "Authorization: Bearer $ADMIN" | jq -c '{ventas,pedidosPorAtender,pedidosEnTaller,avisos}'

printf '\nFlujo completo OK\n'
