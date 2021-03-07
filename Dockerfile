FROM openjdk:8-jre-alpine
EXPOSE 8080
RUN cd target
COPY target/Stats-1.0.0.jar /applications
ENTRYPOINT ["java", "-jar", "Stats-1.0.0.jar"]