FROM openjdk:8-jre-alpine

EXPOSE 8080
ARG CACHEBUST=1
RUN pwd
RUN echo "$PWD"
RUN echo "$CACHEBUST"
COPY . /usr/app/
WORKDIR /usr/app
RUN ls -a
COPY ./Stats-1.0.0.jar /usr/app/
ENTRYPOINT ["java", "-jar", "Stats-1.0.0.jar"]