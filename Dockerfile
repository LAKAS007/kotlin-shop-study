FROM gradle:8.11-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle :app:jar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
