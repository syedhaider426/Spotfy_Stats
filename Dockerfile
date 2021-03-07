FROM openjdk:8-jre-alpine
EXPOSE 8080
WORKDIR /applications
COPY target/Stats-1.0.0.jar /applications/Stats-1.0.0.jar
ENTRYPOINT ["java", "-jar", "Stats-1.0.0.jar"]