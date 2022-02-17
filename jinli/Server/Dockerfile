FROM adoptopenjdk/openjdk11
MAINTAINER cangge "battlefly@gmail.com"
COPY ./target/jinli-0.0.1-SNAPSHOT.jar app.jar
WORKDIR /usr/src/app
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 7001 8080