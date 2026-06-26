# 采油厂油水井作业成本管理系统 - 设计文档

## 一、项目概述

采油厂油水井作业成本管理系统是一套基于 B/S 架构的企业级管理平台，面向采油厂的日常生产管理需求，涵盖：

- 油水井基础信息管理
- 作业施工全流程管理（计划→进行中→完工）
- 作业成本核算（预算→结算→终审三级）
- 可视化仪表盘与 ECharts 数据大屏
- AI 智能问答与成本分析（接入 DeepSeek 大模型）
- 站内消息系统（用户间私信、系统通知）
- 个人主页与用户动态
- 多角色权限控制（管理员/普通用户）

---

## 二、技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.3.8 |
| JDK | Java | 17 |
| ORM | MyBatis + PageHelper | 3.0.4 + 2.0.0 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis + Spring Cache AOP | 7 |
| 前端框架 | Vue 3 + Vite | 6.1 |
| UI 组件 | Element Plus | 2.9.4 |
| 图表 | ECharts | 5.6.0 |
| 状态管理 | Pinia + persist | 3.0.1 |
| HTTP 客户端 | Axios | 1.7.9 |
| AI 模型 | DeepSeek Chat | - |
| JWT | java-jwt | 4.4.0 |
| 构建工具 | Maven | 3.8+ |

---

## 三、系统架构

### 3.1 分层架构

```
┌──────────────────────────────────────────────────┐
│                  前端层 (Vue 3)                    │
│  ┌──────────┬──────────┬────────┬──────────────┐ │
│  │ 管理员端  │  用户端   │ 公开页  │ 组件/工具    │ │
│  │ Layout   │ Layout   │ Login  │ usePermission│ │
│  │ +12页面  │ +9页面   │Register│ BrandHead    │ │
│  └──────────┴──────────┴────────┴──────────────┘ │
│        │ Axios HTTP + token 头                    │
├──────────────────────────────────────────────────┤
│              控制层 (Controller)                   │
│  15 个 @RestController，处理 HTTP 请求和响应        │
│  统一使用 Result 包装类返回                         │
├──────────────────────────────────────────────────┤
│              服务层 (Service)                      │
│  PermissionService ←  AuthorityService            │
│  InfoService       ←  UserService                 │
│  ChatService       ←  RedisService                │
│  + WellService / OperationService / CostService   │
│  + MessageService / DynamicService / EchartsSvc   │
├──────────────────────────────────────────────────┤
│              AOP 切面层                             │
│  LogAspect (@Log)   → 操作日志记录                 │
│  CacheAspect (@AutoCache) → Redis 缓存            │
│  RateLimitAspect (@RateLimit) → 接口限流          │
├──────────────────────────────────────────────────┤
│              数据访问层 (Mapper)                    │
│  12 个 MyBatis Mapper 接口（@Select/@Insert/...）  │
│  + PageHelper 分页插件                            │
├──────────────────────────────────────────────────┤
│              数据层                                │
│  MySQL 8.0 (10 张业务表 + 6 个视图)               │
│  Redis 7 (缓存/限流/在线用户)                     │
└──────────────────────────────────────────────────┘
```

### 3.2 请求处理流程

```
浏览器 / API 客户端
    │
    ▼
LoginCheckInterceptor（拦截器）
    ├─ OPTIONS 请求 → 直接放行（CORS 预检）
    ├─ DEBUG_MODE=true → 放行并记录在线用户
    └─ 正常模式 → 校验 JWT token → 未登录返回 NOT_LOGIN
    │
    ▼
Controller（控制器）
    ├─ 调用 PermissionService.requireXxx() 校验权限
    ├─ 管理员自动放行，普通用户查 tb_authorization
    └─ 不满足则抛 OperationInvalidException
    │
    ▼
Service（业务逻辑层）
    │
    ▼
Mapper（MyBatis 数据访问）
    │
    ▼
MySQL / Redis
```

---

## 四、部署架构

### 4.1 生产部署（Vercel + Cloudflare + Railway）

```
用户浏览器
    │
    ├─ 静态资源 ← Vercel CDN (前端 SPA)
    │
    └─ API 请求 → Cloudflare Worker (API 转发代理)
                        │
                        ▼
                    Railway (Spring Boot 后端)
                        │
                        ├─ MySQL 8.0 (数据库)
                        └─ Redis 7 (缓存)
```

**组件说明：**

| 组件 | 平台 | 用途 |
|------|------|------|
| 前端 | Vercel | 托管 Vue 3 SPA，全球 CDN 加速 |
| API 代理 | Cloudflare Worker | 转发 API 请求，解决跨域 |
| 后端 | Railway | 运行 Spring Boot 应用（Docker 容器化） |
| 数据库 | Railway 插件 | MySQL 8.0 托管服务 |
| 缓存 | Railway 插件 | Redis 7 托管服务 |

### 4.2 本地开发（Docker Compose）

```
docker-compose.yml
    ├─ mysql:8.0        (端口 3307)
    ├─ redis:7-alpine   (端口 6380)
    ├─ backend          (端口 8080, Spring Boot)
    └─ frontend         (端口 80, Nginx + Vue 3)
```

### 4.3 一键启动（Windows）

项目根目录 `start.bat` 自动完成：
1. 检测 Java 17+/Maven/Node.js 环境
2. 检测并启动 MySQL 服务
3. 自动执行 `schema.sql` 建库建表
4. 检测 Redis 连通性（可选）
5. 安装前端依赖（首次）
6. 独立窗口启动后端 (8080) + 前端 (8082)
7. 自动打开浏览器

---

## 五、数据库设计

### 5.1 数据库概述

- **数据库名**: `tb_oil_well_cost`
- **字符集**: `utf8mb4` / `utf8mb4_unicode_ci`
- **建表脚本**: `schema.sql`
- **安全脚本**: `schema-security.sql`

### 5.2 核心表结构

#### 5.2.1 tb_user — 用户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 用户 ID |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | VARCHAR(100) | 密码 |
| role | VARCHAR(20) | 角色: 管理员/普通用户 |
| gender | VARCHAR(10) | 性别 |
| phone | VARCHAR(20) | 手机号 |
| avatar | VARCHAR(500) | 头像路径 |
| background | VARCHAR(500) | 背景图路径 |
| bio | VARCHAR(200) | 个人简介 |
| status | INT DEFAULT 1 | 1=启用, 0=禁用 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### 5.2.2 tb_well — 油水井基础信息表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 井 ID |
| well_name | VARCHAR(100) | 井号 |
| well_type | VARCHAR(20) | 井类型: 油井/水井 |
| well_status | VARCHAR(20) | 状态: 生产/关停/报废/注水 |
| field_name | VARCHAR(100) | 所属油田/区块 |
| layer | VARCHAR(100) | 生产层位 |
| depth | DECIMAL(10,2) | 井深(米) |
| operator | VARCHAR(100) | 负责人 |
| drilling_date | DATE | 投产日期 |
| address | VARCHAR(200) | 地理位置 |
| notes | TEXT | 备注 |
| created_at / updated_at | DATETIME | 时间戳 |

#### 5.2.3 tb_operation — 作业记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 作业 ID |
| well_id | BIGINT FK | 关联井 ID |
| operation_type_id | BIGINT FK | 作业类型 ID |
| operation_name | VARCHAR(200) | 作业名称 |
| start_date / end_date | DATE | 起止日期 |
| team_name / team_leader | VARCHAR | 作业队伍 |
| team_members | INT | 作业人数 |
| status | VARCHAR(20) | 计划/进行中/已完成/暂停 |
| notes | TEXT | 备注 |
| created_at / updated_at | DATETIME | 时间戳 |

#### 5.2.4 tb_operation_type — 作业类型字典

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 类型 ID |
| type_name | VARCHAR(100) | 检泵/压裂/注水/酸化/修井/清蜡/测井/钻井/冲砂/其他 |
| description | VARCHAR(500) | 描述 |

#### 5.2.5 tb_cost — 成本总表

成本核算采用三级架构：**预算 → 结算 → 终审**。

| 字段 | 类型 | 说明 |
|------|------|------|
| code | CHAR(20) PK | 费用编号/作业项目编号 |
| preunit | CHAR(20) | 预算单位（采油队代码） |
| wellcode | CHAR(20) | 井号 |
| premoney | DECIMAL(14,2) | 预算总金额 |
| person | CHAR(20) | 预算编制人 |
| predate | DATE | 预算编制日期 |
| startdate / finish | DATE | 工程起止日期 |
| settleunit | CHAR(20) | 施工/结算单位 |
| content | CHAR(20) | 作业内容 |
| mat1~mat4_code/num/price/sub | 多种 | 四类材料明细（编码/数量/单价/小计） |
| matcost | DECIMAL(14,2) | 材料总成本 |
| humancost | DECIMAL(14,2) | 人工成本 |
| equipcost | DECIMAL(14,2) | 设备成本 |
| othercost | DECIMAL(14,2) | 其他成本 |
| settlecost | DECIMAL(14,2) | 结算总金额 |
| settleperson / settledate | - | 结算经办人/日期 |
| finalcost | DECIMAL(14,2) | 入账/终审金额 |
| finalperson / finaldate | - | 入账/终审人/日期 |

#### 5.2.6 tb_authorization — 授权记录

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT | 被授权用户 ID |
| admin_id | BIGINT | 授权管理员 ID |
| status | TINYINT | 1=有效, 0=失效 |
| perm_create | TINYINT | 增权限 |
| perm_read | TINYINT | 查权限 |
| perm_update | TINYINT | 改权限 |
| perm_delete | TINYINT | 删权限 |
| created_at / updated_at | DATETIME | 时间戳 |

#### 5.2.7 tb_authorization_request — 权限申请记录

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| user_id | BIGINT | 申请人 ID |
| admin_id | BIGINT | 目标管理员 ID |
| status | TINYINT | -1=待处理, 0=已拒绝, 1=已批准 |
| perm_create/read/update/delete | TINYINT | 申请权限 |
| request_message | VARCHAR | 申请理由 |
| created_at / processed_at | DATETIME | 时间戳 |

#### 5.2.8 tb_message — 消息表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 消息 ID |
| sender_id | BIGINT | 发送者 ID |
| receiver_id | BIGINT | 接收者 ID |
| message | TEXT | 消息内容 |
| checked | TINYINT | 0=未读, 1=已读 |
| sent_time | DATETIME | 发送时间 |
| INDEX idx_sender/receiver/conv | - | 联合索引加速会话查询 |

#### 5.2.9 tb_dynamic — 用户动态表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 动态 ID |
| user_id | BIGINT | 用户 ID |
| content | TEXT | 动态内容 |
| images | VARCHAR(2000) | 图片路径（逗号分隔） |
| created_at / updated_at | DATETIME | 时间戳 |

#### 5.2.10 tb_echarts — 图表数据表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 主键 |
| uname | VARCHAR(100) | 图例名称 |
| uvalue | TEXT | 周数据 JSON 数组 |
| statsType | VARCHAR(50) | 统计类型 |
| activityCount / volunteerCount / ... | INT | 统计数据 |

### 5.3 业务视图

| 视图 | 作用 | 核心 SQL |
|------|------|----------|
| v_operation_detail | 作业列表 + 井号 + 作业类型 | `tb_operation LEFT JOIN tb_well LEFT JOIN tb_operation_type` |
| v_cost_detail | 成本明细 + 井号 + 油田 | `tb_cost LEFT JOIN tb_well` |
| v_well_overview | 油水井概览 + 作业/成本统计 | `tb_well LEFT JOIN tb_operation LEFT JOIN tb_cost` |
| v_dashboard_stats | 仪表盘关键指标 | 5 个子查询聚合 |
| v_echarts_data | ECharts 图表数据 | `SELECT * FROM tb_echarts` |
| v_user_public | 用户公开信息（无密码） | `SELECT 除 password 外的所有字段` |

### 5.4 E-R 关系

```
tb_user (1) ──────< tb_message      (用户发送/接收消息)
tb_user (1) ──────< tb_dynamic      (用户发布动态)
tb_user (1) ──────< tb_authorization (用户被授权)
tb_user (1) ──────< tb_authorization_request (用户申请权限)

tb_well (1) ──────< tb_operation    (井关联作业)
tb_well (1) ──────< tb_cost         (井关联成本)

tb_operation_type (1) ──────< tb_operation (作业类型字典)
```

---

## 六、后端架构详解

### 6.1 包结构

```
com.mingbo
  ├── OilWellApplication.java        # Spring Boot 启动类
  ├── anno/                          # 自定义注解
  │   ├── AutoCache.java             # Redis 缓存注解
  │   ├── Log.java                   # 操作日志注解
  │   └── RateLimit.java             # 接口限流注解
  ├── aop/                           # AOP 切面
  │   ├── CacheAspect.java           # @AutoCache 实现
  │   ├── LogAspect.java             # @Log 实现（操作日志记录）
  │   └── RateLimitAspect.java       # @RateLimit 实现（Redis 滑动窗口）
  ├── config/
  │   └── WebMvcConfig.java          # CORS + 拦截器 + 静态资源映射
  ├── controller/                    # 15 个 REST 控制器
  ├── exception/                     # 异常体系
  │   ├── GlobalExceptionHandler.java
  │   ├── OperationInvalidException.java
  │   └── AuthorityRoleErrorException.java
  ├── interceptor/
  │   └── LoginCheckInterceptor.java # JWT 登录拦截
  ├── mapper/                        # 12 个 MyBatis Mapper
  ├── pojo/                          # 26 个 POJO/DTO/VO
  ├── service/                       # 业务接口
  │   └── impl/                      # 实现类
  │       ├── ToolDefinition.java    # AI 工具定义
  │       └── ToolExecutor.java      # AI 工具执行
  └── util/
      └── JwtUtils.java             # JWT 工具类
```

### 6.2 控制器总览

| Controller | 路径 | 说明 | 权限要求 |
|------------|------|------|----------|
| UserController | `/user/**` | 登录/注册/用户列表 | 登录注册无校验，其余管理员 |
| WellController | `/api/well/**` | 油水井 CRUD | CRUD 权限 |
| OperationController | `/api/operation/**` | 作业管理 CRUD | CRUD 权限 |
| CostController | `/api/cost/**` | 成本管理 CRUD | CRUD 权限 |
| OperationTypeController | `/api/operation-type/**` | 作业类型管理 | 无特殊校验 |
| DashboardController | `/api/dashboard/**` | 仪表盘数据 | 无校验（公开） |
| EchartsController | `/api/echarts/**` | ECharts 图表数据 | 无校验（公开） |
| DynamicController | `/dynamic/**` | 用户动态 | 增/查/删权限 |
| MessageController | `/news/**` | 站内消息 | 查/增/改权限 |
| EmployeeController | `/employee/**` | 员工管理 | 仅管理员 |
| AdminSearchController | `/admin/**` | 管理员列表查询 | 查权限 |
| AuthorityController | `/authority/**` | 权限申请/审批/查询 | 业务逻辑内校验 |
| PersonalController | `/personal/**` | 个人信息/上传 | 无特殊校验 |
| NvidiaAIController | `/api/ai/**` | AI 聊天 | 无校验（公开） |
| ResourceController | `/src/**` | 文件上传/管理 | 业务逻辑内校验 |
| CommonController | `/uploads/**` | 通用文件上传 | 无校验 |

### 6.3 权限系统（PermissionService）

采用**双重防护**架构：

```
用户请求 → PermissionService（应用层校验）
               ↓ 通过
        Controller 业务逻辑
               ↓
        MyBatis 执行SQL → MySQL GRANT（数据库层校验）
                            ↓
                       表 / 视图
```

#### 应用层校验

`PermissionService` 提供 5 个方法：

| 方法 | 校验逻辑 |
|------|----------|
| `requireCreate()` | 需 `permCreate = 1` |
| `requireRead()` | 需 `permRead = 1` |
| `requireUpdate()` | 需 `permUpdate = 1` |
| `requireDelete()` | 需 `permDelete = 1` |
| `requireAdmin()` | 必须为"管理员"角色 |

校验流程：
1. `InfoService.getOperateUser()` 从 JWT 获取当前用户 ID
2. 角色为"管理员" → 直接放行
3. 查询 `tb_authorization` OR 聚合用户权限
4. 检查对应 perm 字段，不满足则抛 `OperationInvalidException`

后端校验已应用到：WellController、OperationController、CostController、DynamicController、MessageController、EmployeeController、AdminSearchController。

#### 数据库层安全

- 创建专用用户 `app_secure`，按最小权限原则 GRANT
- 6 个视图限制用户能看到的数据范围
- 未授予 DDL（CREATE/DROP/ALTER）、GRANT 等管理权限

#### 前端权限控制

`usePermission` composable：
- 调用 `getMyPermissions()` API 获取权限
- 管理员自动全权限
- 三个业务页面的增/改/删按钮使用 `v-if="canXxx"` 条件渲染

#### 异常处理

```
OperationInvalidException → HTTP 200 + {code: "0", msg: "无操作权限"}
其他 Exception             → HTTP 200 + {code: "0", msg: "操作失败,请联系管理员"}
```

### 6.4 认证体系

| 组件 | 机制 |
|------|------|
| 登录 | 用户名+密码 → 生成 JWT（含 id/username/role） |
| 请求认证 | `token` 自定义请求头携带 JWT |
| 拦截器 | `LoginCheckInterceptor` 校验 JWT 有效性 |
| 开发模式 | `DEBUG_MODE=true` 跳过真实校验 |
| 在线用户 | Redis Set `online:users` 记录（5 分钟过期） |

### 6.5 AOP 切面

#### @Log — 操作日志

自动记录被注解方法的：操作人、操作时间、类名、方法名、参数、返回值、耗时。存入 `tb_operate_log` 表。

#### @AutoCache — 自动缓存

为被注解方法自动管理 Redis 缓存：
- 支持自定义 key 前缀和 TTL
- 自动生成 key: `prefix:ClassName.methodName(argsHash)`
- 命中/未命中统计（`cache:stats:hits` / `cache:stats:misses`）
- 响应头 `X-Cache-Status: HIT/MISS`

#### @RateLimit — 接口限流

基于 Redis 实现滑动窗口限流：
- 按用户 + 方法组合限流
- 可配置 `maxRequests` 和 `windowSeconds`
- 超过阈值返回"请求过于频繁"

### 6.6 AI 集成

- 接入 DeepSeek Chat API
- 支持工具调用（ToolDefinition / ToolExecutor）
- 自定义 System Prompt（角色设定为油井成本分析助手）
- 配置化 API 地址和 Key

---

## 七、前端架构详解

### 7.1 项目结构

```
oil-well-vue/src/
  ├── App.vue                       # 根组件（Element Plus 中文配置）
  ├── main.js                       # 入口（注册插件/路由/Pinia）
  ├── api/                          # 按模块分文件（15 个 API 模块）
  │   ├── authority.js              # 权限相关 API
  │   ├── user.js                   # 用户相关 API
  │   ├── well.js / cost.js / ...   # 业务模块 API
  │   └── ai.js / aiChat.js         # AI 聊天 API
  ├── components/                   # 公共组件
  │   ├── BrandHead.vue             # 顶部导航栏
  │   ├── BrandAside.vue            # 管理员侧边栏
  │   ├── UserAside.vue             # 用户侧边栏
  │   ├── PageContainer.vue         # 页面容器
  │   └── PageContainerView.vue     # 页面视图容器
  ├── composables/                  # 组合式函数
  │   └── usePermission.js          # 前端权限控制
  ├── router/
  │   └── index.js                  # 路由配置 + 导航守卫
  ├── stores/
  │   ├── index.js                  # Pinia 初始化 + persist
  │   └── modules/
  │       └── user.js               # 用户状态（token + user）
  ├── utils/
  │   └── request.js                # Axios 封装（拦截器）
  └── views/
      ├── admin/                    # 管理员端（12 页面）
      ├── user/                     # 用户端（7 页面）
      └── public/                   # 公开页面（Login / Register / ...）
```

### 7.2 路由设计

| 路由 | 布局 | 页面 | 可见性 |
|------|------|------|--------|
| `/login` | 无 | 登录 | 公开 |
| `/register` | 无 | 注册 | 公开 |
| `/dashboard` | 无 | 独立仪表盘 | 公开 |
| `/admin/*` | Admin Layout | 12 个子页面 | 管理员 |
| `/user/*` | User Layout | 7 个子页面 | 普通用户 |
| `/:pathMatch(.*)*` | - | 重定向到 `/admin` | - |

导航守卫：未登录（无 token）时跳转到 `/login`，登录后自动分流（普通用户→`/user`，管理员→`/admin`）。

### 7.3 两套布局

#### 管理员端（BrandHead + BrandAside）

侧边栏菜单：仪表盘、油水井管理、作业管理、成本管理、ECharts 大屏、员工管理、用户管理、权限管理、AI 助手、消息通知、个人主页、文件管理。

#### 用户端（BrandHead + UserAside）

侧边栏菜单：仪表盘、油水井管理、作业管理、成本管理、AI 助手、消息通知、权限申请、申请记录、个人主页。

两者均采用**毛玻璃（glassmorphism）** 设计风格。

### 7.4 状态管理

- **Pinia** + **pinia-plugin-persistedstate** 实现 token 和用户信息的 localStorage 持久化
- `useUserStore`：管理 token、用户信息、登录/登出
- 页面刷新后自动恢复登录状态

### 7.5 前端权限控制

`usePermission` composable 提供：
- `fetchPermissions()` — 调用 API 获取当前用户 CRUD 权限
- `canCreate / canRead / canUpdate / canDelete` — 四个 computed 属性
- 管理员自动全权限（`isAdmin` 判断）
- 配合后端校验实现双重保护

---

## 八、消息系统

### 8.1 功能

- 用户对用户私信聊天
- 管理员向用户发送系统通知
- 未读消息红点提醒
- 基于 `tb_message` 表的会话式消息（双向索引优化查询）

### 8.2 流程

```
用户 A 发送消息 → MessageController.sendMessage()
    → 校验权限（requireCreate）
    → MessageService.sendMessage()
    → INSERT tb_message (sender_id=A, receiver_id=B)
    → 用户 B 轮询/刷新 → 看到未读消息

系统通知：
AuthorityController.addRequest()
    → 用户申请权限 → MessageService.sendSystemMessage()
    → 向管理员发送"用户 XX 请求权限"通知
```

---

## 九、文件管理

### 9.1 功能

- 用户头像/背景图上传（PersonalController）
- 通用文件上传（CommonController /uploads）
- 管理员文件资源管理（ResourceController /src）

### 9.2 存储

- 本地文件系统存储
- 支持自定义 `upload.dir` 配置
- 默认 `assets/` 目录（头像/背景图）
- 文件访问通过 `WebMvcConfig` 配置的静态资源映射

---

## 十、操作日志

### 10.1 功能

通过 `@Log` 注解 + AOP 切面自动记录所有关键操作：

| 字段 | 来源 |
|------|------|
| 操作人 | JWT token 中的 id |
| 操作时间 | `LocalDateTime.now()` |
| 类名/方法名 | 反射获取 |
| 参数 | `joinPoint.getArgs()` |
| 返回值 | `JSONObject.toJSONString(result)` |
| 耗时 | 方法执行前后时间差 |

存储在 `tb_operate_log` 表中，支持事后审计追踪。

---

## 十一、配置体系

### 11.1 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| PORT | 后端端口 | 8080 |
| MYSQL_URL | 数据库连接 URL | `jdbc:mysql://localhost:3306/tb_oil_well_cost` |
| MYSQL_USER | 数据库用户 | root |
| MYSQL_PASSWORD | 数据库密码 | (空) |
| REDIS_HOST | Redis 地址 | localhost |
| REDIS_PORT | Redis 端口 | 6379 |
| DEEPSEEK_API_URL | DeepSeek API 地址 | `https://api.deepseek.com` |
| DEEPSEEK_API_KEY | DeepSeek API 密钥 | (空) |
| DEEPSEEK_MODEL | DeepSeek 模型 | deepseek-chat |

### 11.2 配置文件

| 文件 | 用途 |
|------|------|
| `application.yml` | 应用主配置（数据源/Redis/AI/MyBatis） |
| `schema.sql` | 建库建表 + 默认数据 |
| `schema-security.sql` | 安全配置（用户 + 视图 + GRANT/REVOKE） |

---

## 十二、API 接口规范

### 12.1 统一响应格式

```json
// 成功
{"code": "1", "msg": "操作成功", "data": {...}}

// 失败
{"code": "0", "msg": "错误描述信息", "data": null}

// 未登录
{"code": "0", "msg": "NOT_LOGIN", "data": null}
```

### 12.2 分页请求/响应

请求参数：`currentPage`, `pageSize`
响应：`PageResult` 包含 `total`, `pages`, `items` 等字段。

### 12.3 鉴权方式

所有 API 通过自定义请求头 `token` 传递 JWT（登录接口除外）。

---

## 十三、项目配置与启动

### 13.1 前后端目录结构

```
E:/projects/reid平台/
  ├── oil-well-system/          # 后端（Spring Boot 3 + Maven）
  │   ├── Dockerfile            # 容器化构建
  │   ├── pom.xml               # Maven 依赖
  │   ├── railway.json          # Railway 部署配置
  │   └── src/main/
  │       ├── java/com/mingbo/  # Java 源码
  │       └── resources/        # 配置文件 + SQL 脚本
  ├── oil-well-vue/             # 前端（Vue 3 + Vite）
  │   ├── Dockerfile            # Nginx 容器化
  │   ├── nginx.conf            # Nginx 反向代理配置
  │   ├── vite.config.js        # Vite 构建配置
  │   └── src/                  # Vue 源码
  ├── cloudflare-worker/        # Cloudflare Worker 代理
  │   └── worker.js
  ├── assets/                   # 上传的静态资源
  ├── schema.sql                # 数据库建表脚本
  ├── docker-compose.yml        # 本地 Docker 部署
  ├── start.bat                 # Windows 一键启动
  ├── README.md                 # 项目说明文档
  ├── PERMISSION.md             # 权限系统设计文档
  └── DESIGN.md                 # 本文档
```

### 13.2 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员（全部权限） |
| user1 | user123 | 普通用户（需申请权限） |

---

## 十四、技术决策记录

| 决策 | 选择 | 原因 |
|------|------|------|
| ORM | MyBatis 注解 | 轻量、SQL 完全可控，适合复杂成本核算查询 |
| 分页 | PageHelper | 与 MyBatis 无缝集成，无需手写分页 |
| 缓存 | Spring Cache + Redis AOP | 声明式缓存，减少侵入性代码 |
| 权限校验 | Controller 层手动调用 | 简单直观，比拦截器/AOP 更灵活（可控制到方法级） |
| 数据库用户 | app_secure GRANT/REVOKE | 最小权限原则，防止直连数据库越权 |
| 前端状态 | Pinia + persist | 自动 localStorage 持久化，刷新不丢登录态 |
| JWT 调试 | DEBUG_MODE 开关 | 开发时跳过复杂 token 校验 |
