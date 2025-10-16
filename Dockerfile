# Stage 1: Build the application using a Maven image
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Build the project, skipping tests to speed up the process
RUN mvn clean package -DskipTests

# Stage 2: Create the final, lightweight runtime image
FROM openjdk:17-slim
WORKDIR /app
# Copy only the built JAR file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar
# Expose the port the application runs on
EXPOSE 8080
# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]