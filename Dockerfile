FROM eclipse-temurin:19-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

EXPOSE 8090

ENTRYPOINT ["java","-jar","target/flashcart-backend-0.0.1-SNAPSHOT.jar"]