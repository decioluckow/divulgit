FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /opt/divulgit
RUN mkdir /opt/divulgit/log
COPY ./target/divulgit-app.jar /opt/divulgit
CMD ["java", "-jar", "/opt/divulgit/divulgit-app.jar"]
