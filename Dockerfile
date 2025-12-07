FROM gradle:8.14-jdk21-noble AS builder
WORKDIR /app
COPY . .
RUN gradle clean build

FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY /build/libs/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080
