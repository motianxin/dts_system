# DTS问题单系统

## 项目简介

DTS（Defect Tracking System）问题单系统是一个完整的问题跟踪和流程管理系统，用于管理软件开发过程中的缺陷跟踪和处理流程。系统实现了从测试人员提单到最终回归测试的完整流程，支持多角色协作和流程控制。

## 功能特性

### 核心功能
- **用户管理**：支持用户注册、登录和角色管理
- **问题单管理**：创建、编辑、删除和查询问题单
- **流程管理**：完整的问题单处理流程，包括提交、审核、处理和回归

### 流程机制
系统实现了以下完整的流程机制：

1. **测试人员提单**：测试人员创建并提交问题单
2. **测试经理审核**：测试经理审核问题单，决定是否通过
3. **开发人员处理**：开发人员处理通过审核的问题单
4. **开发经理审核**：开发经理审核开发人员的处理结果
5. **测试人员回归**：测试人员对通过审核的处理结果进行回归测试

### 角色权限
- **测试人员（TESTER）**：创建问题单、提交问题单、执行回归测试
- **测试经理（TEST_MANAGER）**：审核问题单
- **开发人员（DEVELOPER）**：处理问题单
- **开发经理（DEV_MANAGER）**：审核处理结果

## 技术栈

### 后端
- **Java 21**：编程语言
- **Spring Boot 3.2.0**：应用框架
- **Spring Data JPA**：数据持久化
- **Spring Security**：安全框架
- **MySQL 8.0**：数据库
- **Maven**：项目构建工具

### 前端
- **Vue.js 3**：前端框架
- **Vite**：构建工具
- **JavaScript**：编程语言

### 容器化
- **Docker**：容器化部署
- **Docker Compose**：多容器编排

## 项目结构

```
dts-system/
├── backend/                    # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/dts/system/
│   │       │       ├── controller/     # 控制器
│   │       │       ├── model/          # 数据模型
│   │       │       ├── repository/     # 数据访问层
│   │       │       ├── security/       # 安全配置
│   │       │       └── service/        # 业务逻辑层
│   │       └── resources/
│   │           └── application.properties  # 配置文件
│   ├── Dockerfile              # 后端Docker配置
│   └── pom.xml                 # Maven配置
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── router/             # 路由配置
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── Dockerfile              # 前端Docker配置
│   ├── nginx.conf              # Nginx配置
│   ├── package.json            # 依赖配置
│   └── vite.config.js          # Vite配置
├── docker-compose.yml          # Docker Compose配置
└── README.md                   # 项目说明文档
```

## 环境要求

### 开发环境
- **JDK 21** 或更高版本
- **Maven 3.6+**
- **Node.js 18+**
- **MySQL 8.0**

### 生产环境
- **Docker**
- **Docker Compose**

## 安装和运行

### 方式一：本地开发环境

#### 1. 克隆项目
```bash
git clone <repository-url>
cd dts-system
```

#### 2. 配置数据库
创建MySQL数据库：
```sql
CREATE DATABASE dts_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `backend/src/main/resources/application.properties` 中的数据库配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dts_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
```

#### 3. 运行后端
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

#### 4. 运行前端
```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

### 方式二：Docker容器化部署

#### 1. 构建并启动容器
```bash
cd dts-system
docker-compose up -d --build
```

#### 2. 查看日志
```bash
docker-compose logs -f
```

#### 3. 停止容器
```bash
docker-compose down
```

#### 4. 访问应用
- 前端：http://localhost
- 后端API：http://localhost:8080/api

## 使用说明

### 1. 用户注册和登录
1. 访问前端应用
2. 点击"注册"按钮创建用户账户
3. 使用账户登录系统

### 2. 测试人员提单
1. 登录系统（角色：测试人员）
2. 在首页填写问题单信息（标题、描述、优先级）
3. 点击"提交工单"按钮创建问题单
4. 在问题单列表中点击"提交"按钮，将问题单提交给测试经理审核

### 3. 测试经理审核
1. 登录系统（角色：测试经理）
2. 在问题单列表中找到状态为"SUBMITTED"的问题单
3. 点击"审核"按钮，输入审核状态和审核意见
4. 审核通过后，问题单将转给开发人员处理

### 4. 开发人员处理
1. 登录系统（角色：开发人员）
2. 在问题单列表中找到状态为"DEVELOPING"的问题单
3. 点击"处理"按钮，输入处理结果
4. 处理完成后，问题单将转给开发经理审核

### 5. 开发经理审核
1. 登录系统（角色：开发经理）
2. 在问题单列表中找到状态为"DEVELOPMENT_REVIEWING"的问题单
3. 点击"审核处理"按钮，输入审核状态和审核意见
4. 审核通过后，问题单将转给测试人员回归

### 6. 测试人员回归
1. 登录系统（角色：测试人员）
2. 在问题单列表中找到状态为"REGRESSING"的问题单
3. 点击"回归"按钮，输入回归结果
4. 回归完成后，问题单状态将变为"COMPLETED"

## API文档

### 问题单API
- `POST /api/issues`：创建问题单
- `GET /api/issues`：获取所有问题单
- `GET /api/issues/{id}`：获取指定问题单
- `PUT /api/issues/{id}`：更新问题单
- `DELETE /api/issues/{id}`：删除问题单
- `POST /api/issues/{id}/submit`：提交问题单
- `POST /api/issues/{id}/review`：审核问题单
- `POST /api/issues/{id}/assign-developer`：分配给开发人员
- `POST /api/issues/{id}/resolve`：处理问题单
- `POST /api/issues/{id}/review-resolution`：审核处理结果
- `POST /api/issues/{id}/assign-tester`：分配给测试人员
- `POST /api/issues/{id}/complete-regression`：完成回归

### 用户API
- `POST /api/users/register`：用户注册
- `POST /api/users/login`：用户登录

## 数据库设计

### 用户表（users）
- `id`：主键
- `username`：用户名
- `password`：密码
- `email`：邮箱
- `role`：角色

### 问题单表（issues）
- `id`：主键
- `title`：标题
- `description`：描述
- `status`：状态
- `priority`：优先级
- `assignee_id`：指派人ID
- `reporter_id`：报告人ID
- `created_at`：创建时间
- `updated_at`：更新时间
- `review_status`：审核状态
- `review_comment`：审核意见
- `resolution`：处理结果
- `regression_result`：回归结果
- `process_status`：流程状态

## 贡献指南

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 联系方式

如有问题或建议，请创建Issue或联系项目维护人员。
