# === Stage 1: The Build Stage ===
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven wrapper and pom.xml first
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# --- ADD THIS LINE ---
# Add execute permission to the wrapper script
RUN chmod +x ./mvnw

# Run the wrapper to download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src src

# Run the build!
RUN ./mvnw clean package -DskipTests


# === Stage 2: The Final Runtime Stage ===
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]