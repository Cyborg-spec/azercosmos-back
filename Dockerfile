# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create directory for SQLite database
RUN mkdir -p /app/data

# Copy the built jar
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/methane_leaks.db

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
