FROM openjdk:8-jre-alpine
EXPOSE 8000
COPY ./target /applications
ENTRYPOINT ["java", "-jar", "Stats-1.0.0.jar"]