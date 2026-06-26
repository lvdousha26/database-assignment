# 权限系统设计文档

## 概述

本系统采用**双重防护**架构：**应用层权限校验** + **数据库层安全控制**，确保用户只能执行其被授权的操作。

```
用户请求 → PermissionService（应用层校验）
                ↓ 通过
         Controller 业务逻辑
                ↓
         MyBatis 执行SQL →  MySQL GRANT（数据库层校验）
                              ↓
                         表 / 视图
```

- **应用层**：通过 `PermissionService` 拦截未授权操作，管理员自动放行
- **数据库层**：通过 `app_secure` 用户和 GRANT/REVOKE 限制表级访问，防止直连数据库越权

---

## 一、应用层权限校验

### 1.1 PermissionService

位于 `oil-well-system/src/main/java/com/mingbo/service/PermissionService.java`。

| 方法 | 校验逻辑 |
|------|----------|
| `requireCreate()` | 需要 `permCreate = 1` |
| `requireRead()` | 需要 `permRead = 1` |
| `requireUpdate()` | 需要 `permUpdate = 1` |
| `requireDelete()` | 需要 `permDelete = 1` |
| `requireAdmin()` | 必须为"管理员"角色 |

校验流程：
1. 通过 `InfoService.getOperateUser()` 获取当前用户 ID
2. 查询用户角色，如果是"管理员" → 直接放行
3. 调用 `AuthorityService.getMyPermissions()` 查询 `tb_authorization` 表
4. 检查对应 perm 字段是否为 1，不满足则抛 `OperationInvalidException`

### 1.2 受控 Controller

| Controller | 校验规则 | 端点 |
|---|---|---|
| WellController | requireCreate / Read / Update / Delete | `/api/well/**` |
| OperationController | requireCreate / Read / Update / Delete | `/api/operation/**` |
| CostController | requireCreate / Read / Update / Delete | `/api/cost/**` |
| DynamicController | requireCreate / Read / Delete | `/dynamic/**` |
| MessageController | requireCreate / Read / Update | `/news/**` |
| EmployeeController | requireAdmin（全部端点） | `/employee/**` |
| AdminSearchController | requireRead | `/admin/**` |

**不加校验的端点：** 仪表盘、ECharts、登录注册、个人资料、AI 聊天等公共功能。

### 1.3 前端权限控制

位于 `oil-well-vue/src/composables/usePermission.js`。

```javascript
const { canCreate, canRead, canUpdate, canDelete, fetchPermissions } = usePermission()
```

- 调用 `getMyPermissions()` API 获取当前用户权限
- 管理员角色自动全权限（`isAdmin` 判断）
- 三个管理页面的增/改/删按钮使用 `v-if="canXxx"` 条件渲染

### 1.4 异常处理

位于 `GlobalExceptionHandler.java`：

```java
@ExceptionHandler(OperationInvalidException.class)
public Result handleOperationInvalid(OperationInvalidException ex) {
    return Result.error(ex.getMessage());  // 将具体错误消息返回前端
}
```

`OperationInvalidException` 的消息会直接透传给前端，而非被通用处理器吞掉。

---

## 二、数据库层安全控制

### 2.1 专用数据库用户

位于 `oil-well-system/src/main/resources/schema-security.sql`。

创建 `app_secure` 用户，按最小权限原则仅授予必要权限：

```sql
CREATE USER 'app_secure'@'localhost' IDENTIFIED BY 'secure123456';
```

**授予的权限：**

| 操作类型 | 授予对象 |
|----------|----------|
| SELECT | 所有业务表 + 视图（读） |
| INSERT | 所有业务表（写） |
| UPDATE | 所有业务表（改） |
| DELETE | 所有业务表（删） |
| USAGE | 仅连接权限 |
| EXECUTE | 存储过程执行 |

**未授予的权限：** DDL（CREATE/DROP/ALTER）、GRANT、FILE、SUPER 等管理权限。

### 2.2 切换为安全用户

开发环境默认使用 `root`，生产环境建议切换：

```bash
# 启动时指定
MYSQL_USER=app_secure MYSQL_PASSWORD=secure123456 mvn spring-boot:run
```

或在 `application.yml` 中修改默认值：

```yaml
spring:
  datasource:
    username: ${MYSQL_USER:app_secure}
    password: ${MYSQL_PASSWORD:secure123456}
```

### 2.3 业务视图

| 视图 | 说明 | 用途 |
|------|------|------|
| `v_operation_detail` | 作业 + 井号 + 作业类型 | 作业列表查询 |
| `v_cost_detail` | 成本 + 井号 + 油田信息 | 成本明细查询 |
| `v_well_overview` | 油水井 + 作业/成本统计 | 概览展示 |
| `v_dashboard_stats` | 仪表盘关键指标 | 首页统计 |
| `v_echarts_data` | ECharts 图表数据 | 可视化大屏 |
| `v_user_public` | 用户信息（不含密码） | 用户搜索 |

---

## 三、数据模型

### 3.1 tb_authorization（授权记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT | 被授权用户 ID |
| admin_id | BIGINT | 授权管理员 ID |
| status | TINYINT | 1=有效, 0=失效 |
| perm_create | TINYINT | 增权限 |
| perm_read | TINYINT | 查权限 |
| perm_update | TINYINT | 改权限 |
| perm_delete | TINYINT | 删权限 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 3.2 tb_authorization_request（申请记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 申请人 ID |
| admin_id | BIGINT | 目标管理员 ID |
| status | TINYINT | -1=待处理, 0=已拒绝, 1=已批准 |
| perm_create | TINYINT | 申请增权限 |
| perm_read | TINYINT | 申请查权限 |
| perm_update | TINYINT | 申请改权限 |
| perm_delete | TINYINT | 申请删权限 |
| request_message | VARCHAR | 申请理由 |
| created_at | DATETIME | 申请时间 |
| processed_at | DATETIME | 处理时间 |

---

## 四、权限申请流程

```
用户选择管理员 → 勾选 CRUD 权限 → 提交申请
        ↓
管理员收到消息 → 进入权限管理页面
        ↓
   批准 / 拒绝
        ↓
   批准：tb_authorization INSERT（OR 聚合已有权限）
   拒绝：tb_authorization_request.status = 0
        ↓
用户收到系统通知 → 前端按钮自动显示
```

**OR 聚合逻辑**：同一用户可以从多个管理员获得权限，`getMyPermissions()` 对同一用户的多个授权记录做按位 OR 聚合。

---

## 五、两套前端布局

| 布局 | 用户类型 | 特点 |
|------|----------|------|
| 管理员端 `/admin/*` | 管理员 | BrandHead + BrandAside 侧边栏 + 毛玻璃 |
| 用户端 `/user/*` | 普通用户 | BrandHead + UserAside 侧边栏 + 毛玻璃 |

**路由跳转逻辑**（`Login.vue`）：

```javascript
router.push(user.value.role == '普通用户' ? "/user" : "/admin")
```

管理员端侧边栏含"用户管理"和"权限管理"两个管理专属项，用户端侧边栏则替换为"权限申请"和"申请记录"。

---

## 六、验证方式

1. 管理员登录 → 所有按钮正常显示 → CRUD 操作正常
2. 用户登录（无权限） → 所有 CRUD 按钮隐藏 → API 返回"无操作权限"
3. 用户申请"增"权限并通过 → 看到"新增"按钮 → 编辑/删除仍隐藏
4. 管理员收回权限 → 刷新后按钮重新隐藏
5. 直连 MySQL 使用 `app_secure` → 无法执行 DDL，仅可操作已授权的表
