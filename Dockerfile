# ==========================
# Build Stage
# ==========================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ==========================
# Runtime Stage
# ==========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the generated JAR
COPY --from=build /app/target/ms-granter-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8080

# JVM options (can be overridden at runtime)
ENV JAVA_OPTS=""

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]