# 采油厂油水井作业成本管理系统

基于 B/S (Browser/Server) 架构的采油厂油水井作业成本管理平台，涵盖油水井信息管理、作业成本核算、成本分析预测、消息系统、权限控制等功能。

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3 + Java 17 |
| 数据库访问 | Spring Data JPA + MyBatis 混合模式（MyBatis @Select/@Insert/@Update/@Delete 注解） |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7（Spring Cache AOP） |
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 图表 | ECharts |
| 状态管理 | Pinia |
| AI 集成 | NVIDIA Nemotron（成本分析预测） |

## 部署架构

```
用户浏览器
    |
    v
Vercel (前端静态托管)
    |
    v
Cloudflare Worker (API 转发代理)
    |
    v
Railway (后端 Spring Boot 应用)
    |
    v
MySQL 8.0 (数据库) + Redis 7 (缓存)
```

### 组件说明

- **前端 (Vercel)**: Vue 3 单页应用，部署在 Vercel 全球 CDN
- **API 转发 (Cloudflare Worker)**: 转发前端 API 请求到后端，解决跨域问题
- **后端 (Railway)**: Spring Boot 应用，通过 Docker 容器化部署
- **数据库**: MySQL 8.0，由 Railway 提供托管数据库服务
- **缓存**: Redis 7，由 Railway 提供托管 Redis 服务

## 功能特性

- 油水井管理：油井/水井基础信息管理
- 作业成本核算：作业成本录入、核算、审批
- 成本分析：基于 NVIDIA Nemotron 模型的成本预测分析
- 可视化大屏：ECharts 仪表盘展示关键指标
- 消息系统：消息发送与通知
- 权限管理：用户角色权限控制
- 操作日志：AOP 切面记录所有关键操作

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user1 | user123 | 普通用户 |

---

## 快速开始

### 方式一：一键启动（Windows）

双击项目根目录的 **`start.bat`**，脚本自动完成：

1. 检测 Java/Maven/Node.js 环境，自动切换 JDK 17+
2. 检测 MySQL 端口，未运行则自动启动 MySQL 服务
3. 检查数据库，不存在则自动执行建库脚本
4. 检测 Redis(6379) 端口连通性（可选）
5. 首次运行自动安装前端依赖
6. 独立窗口启动后端 (8080) + 前端 (8082)
7. 等待服务就绪后自动打开浏览器访问 `http://localhost:8082`

> 关闭后端/前端窗口即可停止对应服务。

### 方式二：手动启动

#### 前置条件

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0
- Redis 7+（可选）

#### 1. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 执行建库建表脚本
source schema.sql
```

#### 2. 配置数据库连接

修改 `oil-well-system/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tb_oil_well_cost?useSSL=false
    username: root
    password: 你的密码
```

#### 3. 启动后端

```bash
cd oil-well-system
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`

#### 4. 启动前端

```bash
cd oil-well-vue
npm install
npm run dev
```

前端默认运行在 `http://localhost:8082`

#### 5. 访问系统

打开浏览器访问 `http://localhost:8082`

- 管理员登录：选择角色「管理员」，用户名 `admin`，密码 `123`
- 用户登录：选择角色「用户」，用户名 `zhangsan`，密码 `123`

---

## 部署指南

### 前端部署（Vercel）

1. 将代码推送到 GitHub 仓库
2. 在 [Vercel](https://vercel.com) 中导入该项目
3. 设置构建配置：
   - **Root Directory**: `oil-well-vue`
   - **Framework**: Vite
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. 环境变量：
   ```
   VITE_API_BASE_URL=https://你的CloudflareWorker域名
   ```
5. 点击 Deploy 即可

### Cloudflare Worker 部署（API 转发）

1. 登录 [Cloudflare Dashboard](https://dash.cloudflare.com)
2. 进入 Workers & Pages
3. 创建 Worker，粘贴 `cloudflare-worker/worker.js` 中的代码
4. 将 `BACKEND_URL` 改为 Railway 部署后的实际后端地址
5. 部署后获得 `*.workers.dev` 域名

### 后端部署（Railway）

1. 将代码推送到 GitHub 仓库
2. 在 [Railway](https://railway.app) 中导入该项目
3. Railway 自动检测 `railway.toml` 和 `Dockerfile`
4. 设置环境变量：
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/oil_well_cost?useSSL=false
   SPRING_DATASOURCE_USERNAME=root
   SPRING_DATASOURCE_PASSWORD=<password>
   SPRING_DATA_REDIS_HOST=<redis-host>
   SPRING_DATA_REDIS_PORT=6379
   ```
5. 添加 MySQL 和 Redis 插件
6. 部署完成后获得 `*.up.railway.app` 域名

---

## Docker 部署

### Docker Compose（本地运行）

项目根目录已提供 `docker-compose.yml`，一键启动全部服务：

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

启动后访问：
- 前端：`http://localhost:80`
- 后端 API：`http://localhost:8080`

### 单独构建

**后端镜像：**

```bash
cd oil-well-system
docker build -t oil-well-cost-backend .
docker run -d -p 8080:8080 --name cost-backend oil-well-cost-backend
```

**前端镜像：**

```bash
cd oil-well-vue
docker build -t oil-well-cost-frontend .
docker run -d -p 80:80 --name cost-frontend oil-well-cost-frontend
```

---

## 项目结构

```
├── oil-well-system/    # 后端 Spring Boot 项目
│   ├── src/main/java/com/mingbo/
│   │   ├── anno/          # 自定义注解
│   │   ├── aop/           # AOP 切面（日志、限流、缓存）
│   │   ├── config/        # 配置类（Redis, WebMvc）
│   │   ├── controller/    # REST API 控制器
│   │   ├── exception/     # 全局异常处理
│   │   ├── interceptor/   # 登录拦截器
│   │   ├── mapper/        # MyBatis Mapper 接口
│   │   ├── pojo/          # 实体类 / DTO
│   │   ├── repository/    # Spring Data JPA Repository
│   │   ├── service/       # 业务逻辑层
│   │   └── util/          # 工具类（JWT）
│   └── src/main/resources/
│       ├── application.yml
│       └── system_messages.properties
│
├── oil-well-vue/       # 前端 Vue 3 项目
│   ├── src/
│   │   ├── api/           # API 请求模块
│   │   ├── components/    # 公共组件
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # Pinia 状态管理
│   │   ├── utils/         # 工具函数
│   │   └── views/         # 页面组件
│   └── package.json
│
├── cloudflare-worker/                     # Cloudflare Worker 转发代理
│   └── worker.js
├── start.bat                              # Windows 一键启动脚本
├── schema.sql                             # 数据库初始化脚本
├── docker-compose.yml                     # Docker Compose 配置
├── railway.toml                           # Railway 部署配置
└── README.md                              # 本文档
```
