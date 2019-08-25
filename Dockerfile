FROM adoptopenjdk/openjdk11:latest
RUN mkdir /opt/app
COPY target/calories-1.0-SNAPSHOT-fat.jar /opt/app
COPY target/config.json /opt/app
EXPOSE 8787
CMD ["java", "-jar", "/opt/app/calories-1.0-SNAPSHOT-fat.jar", "-conf", "/opt/app/config.json"]