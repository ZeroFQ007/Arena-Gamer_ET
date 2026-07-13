FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN groupadd -r authgroup && useradd -r -g authgroup authuser
USER authuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]