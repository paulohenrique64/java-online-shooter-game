FROM maven:3.8.5-openjdk-17
WORKDIR /app
COPY ./pom.xml ./pom.xml
RUN mvn -B -f pom.xml dependency:go-offline
COPY ./src ./src
CMD mvn spring-boot:run


