FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

RUN groupadd -r authgroup && useradd -r -g authgroup authuser
USER authuser

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]