FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY demo/ .
RUN ./gradlew bootJar
EXPOSE 8085
CMD ["java", "-jar", "build/libs/demo-0.0.1-SNAPSHOT.jar"]