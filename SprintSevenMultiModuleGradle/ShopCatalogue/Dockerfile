FROM eclipse-temurin:21-jre-jammy
WORKDIR /yp-shop
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/yp-shop/app.jar"]