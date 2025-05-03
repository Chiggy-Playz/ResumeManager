# ------------ Stage 1: Build with Gradle ------------
FROM gradle:8.5-jdk21-alpine AS build

# Copy project files
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

# Build the app
RUN gradle build -x test

# ------------ Stage 2: Run with minimal JDK ------------
FROM eclipse-temurin:21-jdk-alpine

# Copy built jar from previous stage
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

# Expose port
EXPOSE 8338

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]