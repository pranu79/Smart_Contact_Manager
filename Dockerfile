# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file into the container
# Replace 'your-app.jar' with the actual jar name
COPY target/Smart_Contact-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on (default 8080)
EXPOSE 8080

# Command to run the jar
ENTRYPOINT ["java","-jar","app.jar"]
