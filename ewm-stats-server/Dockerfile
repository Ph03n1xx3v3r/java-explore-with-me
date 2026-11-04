FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY ewm-stats-dto/pom.xml ewm-stats-dto/pom.xml
COPY ewm-stats-dto/src ewm-stats-dto/src
COPY ewm-stats-client/pom.xml ewm-stats-client/pom.xml
COPY ewm-stats-client/src ewm-stats-client/src
COPY ewm-stats-server/pom.xml ewm-stats-server/pom.xml
COPY ewm-stats-server/src ewm-stats-server/src
COPY ewm-main-service/pom.xml ewm-main-service/pom.xml
RUN mvn -pl ewm-stats-server -am -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/ewm-stats-server/target/ewm-stats-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
