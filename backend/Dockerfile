# Multi-stage build: compile with the full JDK+Maven, ship only a slim JRE runtime image.
# Works identically across Railway/Render/Fly.io/etc — any host that can build from a Dockerfile.

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Dependencies first, in their own layer — Docker only re-runs this (the slow part) when
# pom.xml actually changes, not on every source edit.
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
