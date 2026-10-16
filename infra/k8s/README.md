# Despliegue didáctico de SIGIP con Kubernetes

Este manifiesto es un ejemplo de la Unidad 3 de Desarrollo de Aplicaciones Web II. Despliega **solo sales-service**; no sustituye Docker Compose ni despliega automáticamente las dependencias.

## Prerrequisitos

El clúster debe tener accesibles servicios Kubernetes llamados `mysql`, `rabbitmq`, `discovery-server` y `catalog-service`, además de un Secret llamado `sigip-secrets` con estas claves:

- `MYSQL_ROOT_PASSWORD`
- `RABBITMQ_USER`
- `RABBITMQ_PASSWORD`
- `JWT_SECRET`

No guardes contraseñas reales en el manifiesto ni en Git.

## Construir y desplegar en Kubernetes de Docker Desktop

Desde la raíz del repositorio, con Kubernetes habilitado en Docker Desktop:

```bash
docker build --build-arg SERVICE=sales-service -t sigip/sales-service:latest .
kubectl create secret generic sigip-secrets --from-literal=MYSQL_ROOT_PASSWORD="TU_CLAVE_MYSQL" --from-literal=RABBITMQ_USER="TU_USUARIO_RABBITMQ" --from-literal=RABBITMQ_PASSWORD="TU_CLAVE_RABBITMQ" --from-literal=JWT_SECRET="TU_SECRETO_JWT"
kubectl apply -f infra/k8s/sales-service.yaml
kubectl rollout status deployment/sales-service
kubectl get pods,svc
```

Ejecuta `kubectl create secret` solo si el Secret aún no existe. Si las dependencias no están desplegadas dentro del clúster, el pod puede iniciar pero no quedará funcional; revisa `kubectl logs deployment/sales-service`.

## Qué demuestra

- Deployment, Service y configuración externa.
- Credenciales separadas en Kubernetes Secret.
- Startup, readiness y liveness probes usando Spring Boot Actuator.
- Requests y limits de CPU/memoria.
- Despliegue y verificación con `kubectl rollout status`.

Es una práctica de despliegue parcial, no una afirmación de que toda la plataforma SIGIP esté migrada a Kubernetes.
