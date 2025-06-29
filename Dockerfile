# Start from a lightweight Java 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy your built Spring Boot JAR into the container
COPY target/student-course-management-1.0-SNAPSHOT.jar app.jar

# Expose the application port (should match server.port in your properties)
EXPOSE 9090

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
