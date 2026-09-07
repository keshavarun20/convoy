# ---------- Build stage ----------
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build

# Install bash (required for mvnw on Alpine)
RUN apk add --no-cache bash

# Copy Maven wrapper files first
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests -B

# ---------- Run stage ----------
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy the JAR built in previous stage
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8082
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
