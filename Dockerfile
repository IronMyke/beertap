FROM maven:3.8.3-openjdk-17

COPY . /project
RUN  cd /project && mvn package

#run the spring boot application
ENTRYPOINT ["java", "-jar","/project/target/beertap-0.0.1-SNAPSHOT.jar"]

