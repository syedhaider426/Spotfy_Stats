FROM openjdk:8-jre-alpine
EXPOSE 8000
COPY ./target /applications
ENTRYPOINT ["java", "-jar", "applications/Stats-1.0.0.jar"]