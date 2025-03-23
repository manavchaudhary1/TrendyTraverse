# Build stage with JDK to unpackage the jar
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Maintainer information
LABEL maintainer="Manav Chaudhary <manavchaudhary041@gmail.com>"

COPY pom.xml .

# Build the application with Maven
RUN mvn clean package -DskipTests

# The application's jar file, passed during the build
ARG JAR_FILE=target/*.jar

# Copy the jar file to the container
COPY ${JAR_FILE} app.jar

# Unpackage the jar file into a target directory
RUN mkdir -p target/dependency && \
    cd target/dependency && \
    jar -xf /app.jar

# Runtime stage with JRE to reduce the image size
FROM eclipse-temurin:21-jre-alpine

# Add volume pointing to /tmp (often used by Spring Boot apps for temporary files)
VOLUME /tmp

# Define the dependency path
ARG DEPENDENCY=/target/dependency

# Copy unpackaged application components from the build stage
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Run the application with the correct classpath and main class
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.manav.cartservice.CartServiceApplication"]

# Run the application with the correct classpath and main class for debugging purposes with JDWP (Java Debug Wire Protocol)
#ENTRYPOINT ["java", "--add-opens", "java.base/java.nio.charset=ALL-UNNAMED" ,"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-cp", "app:app/lib/*", "com.manav.cartservice.CartServiceApplication"]