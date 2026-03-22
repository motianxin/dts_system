# 后端Dockerfile
FROM eclipse-temurin:21-jdk-alpine

# 安装Maven
RUN apk add --no-cache maven

WORKDIR /app

# 复制Maven依赖文件
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline

# 复制源代码
COPY src src

# 构建项目
RUN mvn package -DskipTests

# 暴露端口
EXPOSE 8080

# 运行应用
CMD ["java", "-jar", "target/dts-system-0.0.1-SNAPSHOT.jar"]