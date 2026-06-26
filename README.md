# 采油厂油水井作业成本管理系统

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

基于 B/S (Browser/Server) 架构的采油厂油水井作业成本管理平台，涵盖油水井信息管理、作业成本核算、成本分析预测、AI 智能问答、站内消息、个人主页与动态、权限控制等功能。

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3 + Java 17 |
| 数据库访问 | Spring Data JPA + MyBatis 混合模式（MyBatis @Select/@Insert/@Update/@Delete 注解） |
| 数据库 | MySQL 8.0+ |
| 缓存 | Redis 7（Spring Cache AOP） |
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 图表 | ECharts |
| 状态管理 | Pinia |
| AI 集成 | DeepSeek（成本分析预测与智能问答） |

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
- 成本分析：基于 DeepSeek 大模型的成本预测与智能问答分析
- 可视化大屏：ECharts 仪表盘展示关键指标
- 站内消息：用户对用户、用户对管理员的私信聊天，含未读红点提醒
- 个人主页：用户信息编辑、头像/背景图上传、动态发布与管理
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

- 管理员登录：用户名 `admin`，密码 `admin123`
- 用户登录：用户名 `user1`，密码 `user123`

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

### 一键运行（推荐）

项目提供统一 Docker 镜像，包含 MySQL + Redis + 后端 + 前端全部组件：

```bash
# 拉取并启动
docker run -d --name oil-well -p 82:80 lvdousha26/database-lvdousha:latest
```

首次启动自动初始化数据库（建表 + 种子数据），就绪后访问 `http://localhost:82`。

### Docker Compose

```bash
docker compose up -d
```

### 停止

```bash
docker compose down
```

---

## 项目结构

```
├── oil-well-system/          # 后端 Spring Boot 3 + JDK 17
│   ├── src/main/java/com/mingbo/
│   │   ├── anno/             # 自定义注解（@AutoCache、@Log、@RateLimit）
│   │   ├── aop/              # AOP 切面（CacheAspect、LogAspect、RateLimitAspect）
│   │   ├── config/           # 配置类（WebMvcConfig）
│   │   ├── controller/       # REST API 控制器（15 个）
│   │   ├── exception/        # 全局异常处理 + 自定义异常
│   │   ├── interceptor/      # 登录拦截器（LoginCheckInterceptor）
│   │   ├── mapper/           # MyBatis Mapper 接口（12 个）
│   │   ├── pojo/             # 实体类 / DTO（26 个）
│   │   ├── service/          # 业务接口 + impl 实现
│   │   │   └── impl/         # 含 ToolDefinition/ToolExecutor（AI 工具调用）
│   │   └── util/             # 工具类（JwtUtils）
│   └── src/main/resources/
│       ├── application.yml
│       ├── schema.sql
│       └── system_messages.properties
│
├── oil-well-vue/             # 前端 Vue 3 + Vite 6
│   ├── src/
│   │   ├── api/              # Axios 请求封装（按模块分文件）
│   │   ├── components/       # 公共组件（BrandAside 侧边栏）
│   │   ├── router/           # Vue Router + 导航守卫
│   │   ├── stores/           # Pinia 状态管理（user + token，persist 持久化）
│   │   ├── utils/            # 工具函数
│   │   └── views/
│   │       ├── admin/        # 管理员端（12页）
│   │       │   ├── AdminHome.vue           # 仪表盘首页
│   │       │   ├── AdminEcharts.vue        # ECharts 可视化大屏
│   │       │   ├── CostManagement.vue      # 成本管理
│   │       │   ├── WellManagement.vue      # 油水井管理
│   │       │   ├── UserManagement.vue      # 用户管理
│   │       │   ├── OperationManagement.vue # 作业管理
│   │       │   ├── MessageManagement.vue   # 消息管理
│   │       │   ├── AIChat.vue              # AI 智能问答
│   │       │   ├── AdminAuthority.vue      # 权限管理
│   │       │   ├── AdminEmployee.vue       # 员工管理
│   │       │   ├── AdminFile.vue           # 文件管理
│   │       │   ├── Profile.vue             # 个人设置
│   │       │   └── Layout.vue              # 管理员布局
│   │       ├── user/         # 用户端（6页）
│   │       │   ├── UsersHome.vue           # 用户首页
│   │       │   ├── UsersHead.vue           # 用户主页
│   │       │   ├── UsersAi.vue             # AI 助手
│   │       │   ├── Function.vue            # 功能页面
│   │       │   ├── PermissionRequest.vue   # 权限申请
│   │       │   ├── AuthorityRequestHistory.vue # 权限记录
│   │       │   └── Layout.vue              # 用户布局
│   │       └── public/       # 公开页面
│   │           ├── Login.vue
│   │           ├── Register.vue
│   │           ├── Message.vue
│   │           └── UserCenter.vue
│   └── package.json
│
├── cloudflare-worker/        # Cloudflare Worker API 转发代理
│   └── worker.js
├── assets/                   # 静态资源（头像、背景图等）
├── mysql-data/               # MySQL 本地数据卷
├── start.bat                 # Windows 一键启动脚本（Docker 模式）
├── schema.sql                # 数据库建库建表脚本
├── Dockerfile                # 统一 Docker 镜像（MySQL+Redis+后端+前端）
├── docker-compose.yml        # Docker Compose 一键部署
├── .dockerignore
└── README.md                 # 本文档

## License

[MIT](LICENSE) © 2026 lvdousha
```
