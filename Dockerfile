FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S casaflow && adduser -S casaflow -G casaflow \
    && mkdir -p /app/uploads && chown -R casaflow:casaflow /app

COPY --from=build --chown=casaflow:casaflow /workspace/target/*.jar app.jar

USER casaflow
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
