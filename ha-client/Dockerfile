FROM alpine:latest

# Install java 8
RUN apk add openjdk8
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk

WORKDIR /app

# Use Maven wrapper so we don't need to install Maven
COPY .mvn .mvn
COPY mvnw mvnw

# Copy only pom - this step then only gets ran when dependencies change
COPY pom.xml pom.xml
RUN ./mvnw dependency:go-offline -B

# Copy whole project and build to jar
COPY . .
RUN ./mvnw package -DskipTests

# Run the jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/ha-client.jar"]
