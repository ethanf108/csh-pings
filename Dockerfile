FROM maven:3.8.6-openjdk-18 as maven
COPY csh-pings-backend/src/ ./src/
COPY csh-pings-backend/pom.xml .
RUN mvn package

FROM openjdk:18-jdk-alpine
COPY --from=maven target/*.jar app.jar
RUN apk add zip
CMD java -jar app.jar
