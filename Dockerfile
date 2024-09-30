# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the fat JAR file from the host to the container
COPY build/libs/com.jasmeet.notes-all.jar /app/application.jar

# Expose the port your Ktor app is running on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
