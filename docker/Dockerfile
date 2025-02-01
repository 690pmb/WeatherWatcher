FROM eclipse-temurin:17.0.14_7-jre-jammy

LABEL MAINTAINER 69pmb <pmbroca@gmail.com>

RUN mkdir /opt/app
COPY *.jar /opt/app/app.jar

USER 10001
CMD ["java", "-jar", "/opt/app/app.jar"]
