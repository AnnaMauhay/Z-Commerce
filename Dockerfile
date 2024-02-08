FROM eclipse-temurin:17-jdk-focal
LABEL authors="Anna Liza Mauhay"

WORKDIR /zalando-ecommerce
COPY target/ecommerce-1.0.0-SNAPSHOT.jar /zalando-ecommerce/zalando-ecommerce-springboot.jar
ENTRYPOINT ["java", "-jar", "zalando-ecommerce-springboot.jar"]

ENV PORT 8080
EXPOSE 8081