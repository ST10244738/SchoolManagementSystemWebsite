# === Stage 1: The Build Stage ===
# Uses a full JDK (version 17) and Maven to build the .jar file
FROM maven:3.9-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Run the wrapper to download dependencies
# This is cached, so it's faster on subsequent builds
RUN ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src src

# Run the build!
# This skips tests. Remove -DskipTests if you want to run them.
RUN ./mvnw clean package -DskipTests


# === Stage 2: The Final Runtime Stage ===
# Uses a slim JRE (Java Runtime) which is smaller and more secure
FROM eclipse-temurin:17-jre-focal

# Set a new, clean working directory
WORKDIR /app

# Copy the built .jar file from the 'build' stage
# The '*.jar' wildcard is fine here
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]