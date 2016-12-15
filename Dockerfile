FROM java:openjdk-8-jre-alpine

MAINTAINER Brian Schlining <bschlining@gmail.com>

ENV APP_HOME /opt/vars-kb-server

RUN mkdir -p ${APP_HOME}

COPY target/phylogeny-server-jar-with-dependencies.jar ${APP_HOME}

EXPOSE 4567

ENTRYPOINT $APP_HOME/bin/jetty-main