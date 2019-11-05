FROM maven:3.6.2-amazoncorretto-8 AS build
WORKDIR /app

# Copy only pom - this step then only gets ran when dependencies change
COPY pom.xml pom.xml
RUN mvn dependency:go-offline -B

# Copy whole project and build
COPY . .
RUN mvn package -DskipTests

# User smaller image for deployment
FROM amazoncorretto:8
COPY --from=build /app/target/ha-client-*.jar /app/target/ha-client.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/ha-client.jar"]
