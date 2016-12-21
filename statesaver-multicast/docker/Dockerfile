FROM micgn/rpi-jdk8
#FROM java:8

MAINTAINER Coen Koomen <ckoomen@quintor.nl>

ADD statesaver-multicast-0.0.1-SNAPSHOT.jar /
ADD application.properties /

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/statesaver-multicast-0.0.1-SNAPSHOT.jar"]
