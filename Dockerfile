# === Stage 1: Build the app ===
FROM eclipse-temurin:21-jdk as build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# === Stage 2: Run the app ===
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/IntervuLog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
