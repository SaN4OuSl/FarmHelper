FROM maven:3.8.1-openjdk-17 as BUILD_IMAGE
WORKDIR /home/app
COPY src src
COPY pom.xml .
RUN mvn clean package -DskipTests

FROM openjdk:17-oracle
COPY --from=BUILD_IMAGE  /home/app/target/*.jar /usr/app/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/usr/app/app.jar"]
EXPOSE 8081