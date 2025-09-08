FROM gradle:8.9-jdk21 AS build

WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon

FROM eclipse-temurin:21-jre-alpine AS runner

# Crear usuario no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar

# Optimización JVM
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
