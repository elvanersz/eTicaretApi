FROM openjdk:17
WORKDIR /app
ADD target/E-Ticaret-API-0.0.1-SNAPSHOT.jar e-ticaret-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "e-ticaret-api-0.0.1-SNAPSHOT.jar"]
