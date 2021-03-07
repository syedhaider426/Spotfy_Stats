FROM openjdk:8-jre-alpine

EXPOSE 8080

RUN ls -a
RUN cd build
RUN cd libs
COPY ./Stats-1.0.0.jar /usr/app/
WORKDIR /usr/app

ENTRYPOINT ["java", "-jar", "Stats-1.0.0.jar"]