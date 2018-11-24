FROM openjdk:11.0.1-jdk

MAINTAINER ranjith

# Env variables
ENV NODE_ENV=dev
ENV PORT=8080

WORKDIR /var/app

COPY ./target/scala-2.12/appuchi-server.jar /var/app
COPY ./run.sh /var/app

EXPOSE $PORT

RUN chmod +x /var/app/run.sh

ENTRYPOINT ["/bin/bash","./run.sh"]
