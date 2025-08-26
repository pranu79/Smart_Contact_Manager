# Stage 1: Build the JAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the JAR file (skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the Spring Boot app
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Ensure Render/Heroku uses dynamic port, fallback 8080
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
