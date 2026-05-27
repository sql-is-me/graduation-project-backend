# 构建阶段 —— 使用 Maven 构建整个多模块项目，只打包 gateway-service
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /build

# 复制整个后端项目（context 为 graduation-project-backend 目录）
COPY . .

# -pl gateway-service -am：只构建 gateway-service 及其上游依赖模块（common, api-inner）
RUN mvn clean package -pl gateway-service -am -DskipTests -q

# 运行阶段 —— 仅包含 JRE，镜像体积更小
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /build/gateway-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
