FROM eclipse-temurin:21-jre-jammy
WORKDIR /yp-payment
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/yp-payment/app.jar"]