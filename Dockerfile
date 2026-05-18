# Stage 1: Build
FROM maven:3.9.15-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Stage 2: Runtime (Minimal)
FROM eclipse-temurin:25-jre-ubi10-minimal

# Install kubectl
RUN apt-get update && \
    apt-get install -y curl && \
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

WORKDIR /app

COPY --from=build /app/target/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
