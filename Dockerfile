# Build stage
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon

# Run stage
FROM eclipse-temurin:25-jre
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
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "app.jar"]
