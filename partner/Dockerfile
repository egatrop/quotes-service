# Create image
FROM openjdk:8-jdk-alpine
COPY partner-service-1.0-all.jar /usr/local/lib/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
