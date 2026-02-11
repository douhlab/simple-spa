#FROM ghcr.io/migrosonline/java21:1.0.58
#
#COPY --chown=app:app /target/*.jar /deployments

# ---- build stage ----
FROM maven:3.9.10-eclipse-temurin-21 AS build
WORKDIR /simple-spa
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- runtime stage ----
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /simple-spa

# Set the active profile for Docker
ENV SPRING_PROFILES_ACTIVE=docker

COPY --from=build /simple-spa/target/*.jar simple-spa.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "simple-spa.jar"]
