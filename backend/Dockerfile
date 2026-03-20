# 后端Dockerfile
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# 复制Maven依赖文件
COPY pom.xml .
COPY .mvn .mvn

# 下载依赖
RUN ./mvnw dependency:go-offline

# 复制源代码
COPY src src

# 构建项目
RUN ./mvnw package -DskipTests

# 暴露端口
EXPOSE 8080

# 运行应用
CMD ["java", "-jar", "target/dts-system-0.0.1-SNAPSHOT.jar"]