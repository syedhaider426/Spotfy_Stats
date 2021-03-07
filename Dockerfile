FROM openjdk:8-jre-alpine
EXPOSE 8080
RUN cd target
COPY ./ /applications
ENTRYPOINT ["java", "-jar", "Stats-1.0.0.jar"]