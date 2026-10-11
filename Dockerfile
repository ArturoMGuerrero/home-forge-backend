FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S homeforge && adduser -S homeforge -G homeforge \
    && mkdir -p /app/uploads && chown -R homeforge:homeforge /app

COPY --from=build --chown=homeforge:homeforge /workspace/target/*.jar app.jar

# Perfil de producción: exige JWT_SECRET y lee la IP real detrás del proxy.
ENV SPRING_PROFILES_ACTIVE=prod \
    UPLOADS_DIRECTORY=/app/uploads \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

USER homeforge
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health/liveness > /dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
