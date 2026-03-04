FROM gradle:8.11-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle :app:installDist --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/app/build/install/app .
EXPOSE 8080
CMD ["bin/app"]
