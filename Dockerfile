# Stage 1: Build
FROM maven:3.9.15-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Stage 2: Runtime (Minimal)
FROM eclipse-temurin:25-jre-ubi10-minimal

# Install curl + kubectl
RUN microdnf install -y curl && \
    microdnf clean all && \
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    chmod +x kubectl && \
    mv kubectl /usr/local/bin/kubectl

WORKDIR /app

COPY --from=build /app/target/app.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
