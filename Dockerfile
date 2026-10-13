# syntax=docker/dockerfile:1
# Imagen única para cualquier microservicio de SIGIP.
#   docker build --build-arg SERVICE=auth-service -t sigip/auth-service .
# Etapa 1: compila con Maven (multi-módulo). Etapa 2: solo el JRE y el .jar (imagen liviana).

FROM maven:3.9-eclipse-temurin-25 AS build
ARG SERVICE
WORKDIR /src
COPY pom.xml .
COPY backend backend
# La caché de ~/.m2 evita volver a descargar dependencias en cada build
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q -pl backend/${SERVICE} -am package -DskipTests \
    && cp backend/${SERVICE}/target/${SERVICE}.jar /app.jar

FROM eclipse-temurin:25-jre
RUN useradd --system --uid 1001 sigip
USER sigip
WORKDIR /app
COPY --from=build /app.jar app.jar
# Ajusta la memoria de la JVM al límite del contenedor
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
