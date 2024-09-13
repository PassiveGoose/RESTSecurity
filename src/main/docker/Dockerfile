FROM openjdk:17-jdk-slim
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} spring-boot_security-demo-0.0.1-SNAPSHOT.jar.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "spring-boot_security-demo-0.0.1-SNAPSHOT.jar.jar"]