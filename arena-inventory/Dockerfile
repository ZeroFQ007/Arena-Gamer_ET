FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

RUN groupadd -r authgroup && useradd -r -g authgroup authuser
USER authuser

EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]