# Use an official OpenJDK 21 image as a parent.
FROM openjdk:21-jdk-slim

# Set the working directory inside the container.
WORKDIR /app

# Copy the packaged JAR file into the container.
COPY target/booknest-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port your application listens on.
EXPOSE 8080

# Define the command to run your application when the container starts.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]