FROM gradle:8.11-jdk17 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY domain/build.gradle.kts domain/
COPY services/build.gradle.kts services/
COPY infrastructure/build.gradle.kts infrastructure/
COPY api/build.gradle.kts api/
COPY app/build.gradle.kts app/
RUN gradle dependencies --no-daemon || true
COPY . .
RUN gradle :app:installDist --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/app/build/install/app .
EXPOSE 8080
CMD ["bin/app"]
