FROM alpine:latest as build

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

# Copy source and build to jar
COPY src src
RUN ./mvnw package -DskipTests

# New Docker image for deployment - doesn't include mvn deps or src
FROM alpine:latest

# Install java 8
RUN apk add openjdk8
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk

WORKDIR /app

COPY --from=build /app/target/ha-client-*.jar /app/ha-client.jar

# Run the jar
EXPOSE 8080
VOLUME ["/app/logs"]
ENTRYPOINT ["java", "-jar", "/app/ha-client.jar"]

