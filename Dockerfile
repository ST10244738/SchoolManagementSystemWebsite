# === Stage 1: The Build Stage ===
# Use a JDK 21 image
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the Maven wrapper and pom.xml first
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Add execute permission to the wrapper script
RUN chmod +x ./mvnw

# Run the wrapper to download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src src

# Run the build!
RUN ./mvnw clean package -DskipTests


# === Stage 2: The Final Runtime Stage ===
# Use a JRE 21 image
FROM eclipse-temurin:21-jre-focal
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]