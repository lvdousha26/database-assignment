-- 采油厂油水井作业成本管理系统 数据库建表脚本
-- 数据库: tb_oil_well_cost

CREATE DATABASE IF NOT EXISTS tb_oil_well_cost DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tb_oil_well_cost;

-- 用户表
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色: 管理员/普通用户',
  gender VARCHAR(10) DEFAULT '男',
  phone VARCHAR(20),
  avatar VARCHAR(500),
  status INT DEFAULT 1 COMMENT '1:启用 0:禁用',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 油水井基础信息表
DROP TABLE IF EXISTS tb_well;
CREATE TABLE tb_well (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  well_name VARCHAR(100) NOT NULL COMMENT '井号',
  well_type VARCHAR(20) NOT NULL COMMENT '井类型: 油井/水井',
  well_status VARCHAR(20) DEFAULT '生产' COMMENT '状态: 生产/关停/报废/注水',
  field_name VARCHAR(100) COMMENT '所属油田/区块',
  layer VARCHAR(100) COMMENT '生产层位',
  depth DECIMAL(10,2) COMMENT '井深(米)',
  operator VARCHAR(100) COMMENT '负责人',
  drilling_date DATE COMMENT '投产日期',
  address VARCHAR(200) COMMENT '地理位置',
  notes TEXT COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 作业类型表
DROP TABLE IF EXISTS tb_operation_type;
CREATE TABLE tb_operation_type (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type_name VARCHAR(100) NOT NULL COMMENT '作业类型名称',
  description TEXT COMMENT '描述',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 作业记录表
DROP TABLE IF EXISTS tb_operation;
CREATE TABLE tb_operation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  well_id BIGINT NOT NULL COMMENT '关联井ID',
  operation_type_id BIGINT NOT NULL COMMENT '作业类型ID',
  operation_name VARCHAR(200) COMMENT '作业名称',
  start_date DATE COMMENT '开始日期',
  end_date DATE COMMENT '结束日期',
  team_name VARCHAR(100) COMMENT '作业队伍',
  team_leader VARCHAR(50) COMMENT '队长',
  team_members INT DEFAULT 0 COMMENT '作业人数',
  status VARCHAR(20) DEFAULT '计划' COMMENT '状态: 计划/进行中/已完成/暂停',
  notes TEXT COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (well_id) REFERENCES tb_well(id) ON DELETE CASCADE,
  FOREIGN KEY (operation_type_id) REFERENCES tb_operation_type(id)
);

-- 成本类别表
DROP TABLE IF EXISTS tb_cost_category;
CREATE TABLE tb_cost_category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL COMMENT '成本类别名称',
  parent_id BIGINT DEFAULT NULL COMMENT '父类别ID',
  description TEXT COMMENT '描述',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (parent_id) REFERENCES tb_cost_category(id)
);

-- 成本明细表
DROP TABLE IF EXISTS tb_cost_detail;
CREATE TABLE tb_cost_detail (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  operation_id BIGINT NOT NULL COMMENT '关联作业ID',
  category_id BIGINT NOT NULL COMMENT '成本类别ID',
  item_name VARCHAR(200) COMMENT '费用项目',
  quantity DECIMAL(12,2) DEFAULT 1 COMMENT '数量',
  unit_price DECIMAL(12,2) DEFAULT 0 COMMENT '单价',
  amount DECIMAL(14,2) DEFAULT 0 COMMENT '金额',
  cost_date DATE COMMENT '发生日期',
  payee VARCHAR(100) COMMENT '收款方/供应商',
  notes TEXT COMMENT '备注',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (operation_id) REFERENCES tb_operation(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES tb_cost_category(id)
);

-- 插入默认数据

-- 默认管理员账号 (密码: admin123)
INSERT INTO tb_user (username, password, role, status) VALUES
('admin', 'admin123', '管理员', 1),
('user1', 'user123', '普通用户', 1);

-- 默认作业类型
INSERT INTO tb_operation_type (type_name, description) VALUES
('修井作业', '油水井维修、检泵等作业'),
('压裂作业', '水力压裂增产作业'),
('酸化作业', '酸化处理增注作业'),
('钻井作业', '新井钻井作业'),
('测井作业', '地球物理测井'),
('注水作业', '注水调整作业'),
('检泵作业', '抽油泵检查维修'),
('清蜡作业', '油井清蜡作业'),
('冲砂作业', '井筒冲砂作业'),
('其他作业', '其他类型的作业');

-- 默认成本类别
INSERT INTO tb_cost_category (category_name, description) VALUES
('材料费', '作业消耗的材料费用'),
('人工费', '作业人员工资及补贴'),
('设备费', '设备租赁及折旧费用'),
('运输费', '物资运输费用'),
('外协费', '外部协作单位费用'),
('管理费', '项目管理费用'),
('其他费用', '其他相关费用');

-- 示例油水井数据
INSERT INTO tb_well (well_name, well_type, well_status, field_name, layer, depth, operator, drilling_date, address) VALUES
('KQ-001', '油井', '生产', '克拉玛依油田', 'S1层', 1850.50, '张工', '2015-03-20', '克拉玛依区采油一厂'),
('KQ-002', '油井', '生产', '克拉玛依油田', 'S2层', 2100.00, '李工', '2016-07-15', '克拉玛依区采油一厂'),
('KS-001', '水井', '注水', '克拉玛依油田', 'S1层', 1780.00, '王工', '2017-01-10', '克拉玛依区采油二厂'),
('KQ-003', '油井', '关停', '克拉玛依油田', 'T1层', 2500.00, '赵工', '2014-11-05', '克拉玛依区采油一厂');
