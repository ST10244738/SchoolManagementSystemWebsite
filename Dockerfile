# Use an official OpenJDK image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the entire backend project into the container
COPY . .

# Build the app (choose Gradle or Maven)
# Comment out the one you don't use

# For Maven:
RUN ./mvnw -DskipTests package || mvn -DskipTests package

# For Gradle:
# RUN ./gradlew build -x test || gradle build -x test

# Expose port (Render uses dynamic PORT)
EXPOSE 8080

# Run the application (Maven target or Gradle build)
CMD ["sh", "-c", "java -jar $(find target -name '.jar' || find build/libs -name '.jar')"]