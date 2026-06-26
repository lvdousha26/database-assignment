-- ============================================================
-- 数据库安全配置：用户创建 + 权限授予 + 视图
-- 在 schema.sql 建表完成后执行此脚本
-- ============================================================
USE tb_oil_well_cost;

-- ============================================================
-- 1. 创建应用专用用户（密码可在生产环境修改）
-- ============================================================
-- 只读用户：仅能 SELECT
-- CREATE USER IF NOT EXISTS 'app_read'@'localhost' IDENTIFIED BY 'read123456';

-- 写入用户：可增删改
-- CREATE USER IF NOT EXISTS 'app_write'@'localhost' IDENTIFIED BY 'write123456';

-- 统一应用用户（兼具读写，但通过 GRANT 细粒度控制表级权限）
-- DROP USER IF EXISTS 'app_secure'@'localhost';
CREATE USER IF NOT EXISTS 'app_secure'@'localhost' IDENTIFIED BY 'secure123456';

-- ============================================================
-- 2. 创建业务视图
-- ============================================================

-- 2.1 作业明细视图（作业 + 井号 + 作业类型名称）
CREATE OR REPLACE VIEW v_operation_detail AS
SELECT
    o.id,
    o.operation_name,
    w.well_name,
    w.well_type,
    w.field_name,
    ot.type_name AS operation_type_name,
    o.start_date,
    o.end_date,
    o.team_name,
    o.team_leader,
    o.team_members,
    o.status,
    o.notes,
    o.created_at,
    o.updated_at
FROM tb_operation o
LEFT JOIN tb_well w ON o.well_id = w.id
LEFT JOIN tb_operation_type ot ON o.operation_type_id = ot.id;

-- 2.2 成本明细视图（成本 + 井号 + 概况）
CREATE OR REPLACE VIEW v_cost_detail AS
SELECT
    c.code,
    c.wellcode,
    w.well_name,
    w.well_type,
    w.field_name,
    c.preunit,
    c.premoney,
    c.person,
    c.predate,
    c.startdate,
    c.finish,
    c.settleunit,
    c.content,
    c.matcost,
    c.humancost,
    c.equipcost,
    c.othercost,
    c.settlecost,
    c.settleperson,
    c.settledate,
    c.finalcost,
    c.finalperson,
    c.finaldate
FROM tb_cost c
LEFT JOIN tb_well w ON c.wellcode = w.well_name;

-- 2.3 油水井概览视图（井 + 作业次数 + 总成本）
CREATE OR REPLACE VIEW v_well_overview AS
SELECT
    w.id,
    w.well_name,
    w.well_type,
    w.well_status,
    w.field_name,
    w.layer,
    w.depth,
    w.operator,
    w.drilling_date,
    w.address,
    COUNT(DISTINCT o.id) AS operation_count,
    COUNT(DISTINCT c.code) AS cost_count
FROM tb_well w
LEFT JOIN tb_operation o ON o.well_id = w.id
LEFT JOIN tb_cost c ON c.wellcode = w.well_name
GROUP BY w.id;

-- 2.4 仪表盘统计视图
CREATE OR REPLACE VIEW v_dashboard_stats AS
SELECT
    (SELECT COUNT(*) FROM tb_well) AS total_wells,
    (SELECT COUNT(*) FROM tb_well WHERE well_status = '生产') AS active_wells,
    (SELECT COUNT(*) FROM tb_operation) AS total_operations,
    (SELECT COUNT(*) FROM tb_operation WHERE status = '进行中') AS active_operations,
    (SELECT COUNT(*) FROM tb_cost) AS total_costs,
    (SELECT COUNT(*) FROM tb_user) AS total_users;

-- 2.5 ECharts 图表数据视图
CREATE OR REPLACE VIEW v_echarts_data AS
SELECT * FROM tb_echarts;

-- 2.6 用户公开信息视图（不暴露密码）
CREATE OR REPLACE VIEW v_user_public AS
SELECT id, username, role, gender, phone, avatar, background, bio, status, created_at
FROM tb_user;

-- ============================================================
-- 3. 授予权限（基于最小权限原则）
-- ============================================================

-- 3.1 基础权限：连接和数据库使用
GRANT USAGE ON *.* TO 'app_secure'@'localhost';
GRANT EXECUTE ON tb_oil_well_cost.* TO 'app_secure'@'localhost';

-- 3.2 SELECT 权限：允许读取所有业务视图和必要的表
GRANT SELECT ON tb_oil_well_cost.v_operation_detail TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.v_cost_detail TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.v_well_overview TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.v_dashboard_stats TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.v_echarts_data TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.v_user_public TO 'app_secure'@'localhost';

-- 视图依赖的基础表也需要 SELECT 权限（MySQL 要求）
GRANT SELECT ON tb_oil_well_cost.tb_well TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_operation TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_operation_type TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_cost TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_echarts TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_user TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_message TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_dynamic TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_authorization TO 'app_secure'@'localhost';
GRANT SELECT ON tb_oil_well_cost.tb_authorization_request TO 'app_secure'@'localhost';

-- 3.3 INSERT 权限：仅允许对业务表插入
GRANT INSERT ON tb_oil_well_cost.tb_well TO 'app_secure'@'localhost';
GRANT INSERT ON tb_oil_well_cost.tb_operation TO 'app_secure'@'localhost';
GRANT INSERT ON tb_oil_well_cost.tb_cost TO 'app_secure'@'localhost';
GRANT INSERT ON tb_oil_well_cost.tb_message TO 'app_secure'@'localhost';
GRANT INSERT ON tb_oil_well_cost.tb_dynamic TO 'app_secure'@'localhost';
GRANT INSERT ON tb_oil_well_cost.tb_authorization_request TO 'app_secure'@'localhost';
GRANT INSERT ON tb_oil_well_cost.tb_authorization TO 'app_secure'@'localhost';
GRANT INSERT ON tb_oil_well_cost.tb_user TO 'app_secure'@'localhost';

-- 3.4 UPDATE 权限：仅允许对业务表更新
GRANT UPDATE ON tb_oil_well_cost.tb_well TO 'app_secure'@'localhost';
GRANT UPDATE ON tb_oil_well_cost.tb_operation TO 'app_secure'@'localhost';
GRANT UPDATE ON tb_oil_well_cost.tb_cost TO 'app_secure'@'localhost';
GRANT UPDATE ON tb_oil_well_cost.tb_user TO 'app_secure'@'localhost';
GRANT UPDATE ON tb_oil_well_cost.tb_message TO 'app_secure'@'localhost';
GRANT UPDATE ON tb_oil_well_cost.tb_dynamic TO 'app_secure'@'localhost';
GRANT UPDATE ON tb_oil_well_cost.tb_authorization TO 'app_secure'@'localhost';
GRANT UPDATE ON tb_oil_well_cost.tb_authorization_request TO 'app_secure'@'localhost';

-- 3.5 DELETE 权限：仅允许对业务表删除
GRANT DELETE ON tb_oil_well_cost.tb_well TO 'app_secure'@'localhost';
GRANT DELETE ON tb_oil_well_cost.tb_operation TO 'app_secure'@'localhost';
GRANT DELETE ON tb_oil_well_cost.tb_cost TO 'app_secure'@'localhost';
GRANT DELETE ON tb_oil_well_cost.tb_message TO 'app_secure'@'localhost';
GRANT DELETE ON tb_oil_well_cost.tb_dynamic TO 'app_secure'@'localhost';
GRANT DELETE ON tb_oil_well_cost.tb_user TO 'app_secure'@'localhost';
GRANT DELETE ON tb_oil_well_cost.tb_authorization TO 'app_secure'@'localhost';
GRANT DELETE ON tb_oil_well_cost.tb_authorization_request TO 'app_secure'@'localhost';

-- 3.6 禁止直接访问敏感表（安全表只能通过应用逻辑操作）
-- 即使是 root 也应遵循此原则

-- ============================================================
-- 4. 撤销不必要的权限（示例：撤销对敏感列的 UPDATE）
-- ============================================================
-- 禁止应用用户修改用户角色（只能通过应用逻辑）
-- REVOKE UPDATE (role) ON tb_oil_well_cost.tb_user FROM 'app_secure'@'localhost';
-- 注：MySQL 不支持列级 REVOKE 已授予的列权限，需 GRANT 时精确控制

-- ============================================================
-- 5. 刷新权限
-- ============================================================
FLUSH PRIVILEGES;

-- ============================================================
-- 6. 查看权限验证
-- ============================================================
-- SHOW GRANTS FOR 'app_secure'@'localhost';
-- SELECT * FROM information_schema.TABLE_PRIVILEGES WHERE GRANTEE = "'app_secure'@'localhost'";
