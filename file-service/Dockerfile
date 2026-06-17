# 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY . .
RUN mvn clean package -pl file-service -am -DskipTests -q

# 运行阶段
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 预创建文件存储目录（与 config-docker.sql 中 file-service-dev.yml 的路径对应）
RUN mkdir -p /app/assets

COPY --from=build /build/file-service/target/*.jar app.jar

COPY assets/ /app/assets/

EXPOSE 9900
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
