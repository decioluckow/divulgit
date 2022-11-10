FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /opt/divulgit
RUN mkdir /opt/divulgit/logs
COPY ./target/divulgit-app.jar /opt/divulgit
WORKDIR /opt/divulgit
CMD ["java", "-jar", "divulgit-app.jar"]