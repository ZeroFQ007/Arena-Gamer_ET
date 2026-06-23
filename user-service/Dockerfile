FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

RUN addgroup -S authgroup && adduser -S authuser -G authgroup
USER authuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]