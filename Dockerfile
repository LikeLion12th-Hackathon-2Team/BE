# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Spring Boot jar file into the container
COPY build/libs/LionHackaton-0.0.1-SNAPSHOT.jar .

# Copy the SSL certificate into the container
COPY keystore.p12 /app/keystore.p12

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "LionHackaton-0.0.1-SNAPSHOT.jar"]
