# 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY . .
RUN mvn clean package -pl transaction-service -am -DskipTests -q

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /build/transaction-service/target/*.jar app.jar
EXPOSE 9903
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
