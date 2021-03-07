FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY ./target /applications
ENTRYPOINT ["java", "-jar", "Stats-1.0.0.jar"]