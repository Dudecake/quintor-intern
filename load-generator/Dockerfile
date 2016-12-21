FROM registry.cluster.local/rpi-jdk8

MAINTAINER Coen Koomen <ckoomen@quintor.nl>

ADD target/load-generator-0.0.1-SNAPSHOT.jar /
ADD application.properties /

EXPOSE 8080

WORKDIR /

ENTRYPOINT ["java", "-jar", "./load-generator-0.0.1-SNAPSHOT.jar"]
