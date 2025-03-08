# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy Maven files for dependency caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd
RUN mvn dependency:go-offline -B

# Copy source code and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-focal

# Set working directory
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/TimeSheetPfe-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Environment variables for configuration (default values can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="" \
    SPRING_DATASOURCE_URL="jdbc:postgresql://ep-black-dream-a2l4bfzr-pooler.eu-central-1.aws.neon.tech/timesheetapp?sslmode=require" \
    SPRING_DATASOURCE_USERNAME="timesheetapp_owner" \
    SPRING_DATASOURCE_PASSWORD="npg_udNUbLA7S9Gq"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]