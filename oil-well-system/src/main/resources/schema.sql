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
  background VARCHAR(500),
  bio VARCHAR(200) DEFAULT '' COMMENT '个人简介',
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
  FOREIGN KEY (well_id) REFERENCES tb_well(id) ON DELETE CASCADE
);

-- 成本总表（预算→结算→终审）
DROP TABLE IF EXISTS tb_cost;
CREATE TABLE tb_cost (
  code CHAR(20) NOT NULL PRIMARY KEY COMMENT '费用编号/作业项目编号',
  preunit CHAR(20) NOT NULL COMMENT '预算单位（采油队代码）',
  wellcode CHAR(20) NOT NULL COMMENT '井号（油水井编号）',
  premoney DECIMAL(14,2) COMMENT '预算总金额',
  person CHAR(20) COMMENT '预算编制人',
  predate DATE NOT NULL COMMENT '预算编制日期',
  startdate DATE COMMENT '工程开工日期',
  finish DATE COMMENT '工程完工日期',
  settleunit CHAR(20) NOT NULL COMMENT '施工/结算单位',
  content CHAR(20) COMMENT '作业内容/施工内容',
  mat1_code CHAR(20),
  mat1_num INT DEFAULT 0 CHECK (mat1_num >= 0),
  mat1_price DECIMAL(14,2) CHECK (mat1_price >= 0),
  mat1_sub DECIMAL(14,2),
  mat2_code CHAR(20),
  mat2_num INT DEFAULT 0 CHECK (mat2_num >= 0),
  mat2_price DECIMAL(14,2) CHECK (mat2_price >= 0),
  mat2_sub DECIMAL(14,2),
  mat3_code CHAR(20),
  mat3_num INT DEFAULT 0 CHECK (mat3_num >= 0),
  mat3_price DECIMAL(14,2) CHECK (mat3_price >= 0),
  mat3_sub DECIMAL(14,2),
  mat4_code CHAR(20),
  mat4_num INT DEFAULT 0 CHECK (mat4_num >= 0),
  mat4_price DECIMAL(14,2) CHECK (mat4_price >= 0),
  mat4_sub DECIMAL(14,2),
  matcost DECIMAL(14,2) DEFAULT 0 CHECK (matcost >= 0) COMMENT '材料总成本',
  humancost DECIMAL(14,2) DEFAULT 0 CHECK (humancost >= 0) COMMENT '人工成本',
  equipcost DECIMAL(14,2) DEFAULT 0 CHECK (equipcost >= 0) COMMENT '设备成本',
  othercost DECIMAL(14,2) DEFAULT 0 CHECK (othercost >= 0) COMMENT '其他成本',
  settlecost DECIMAL(14,2) DEFAULT 0 CHECK (settlecost >= 0) COMMENT '结算总金额',
  settleperson CHAR(20) COMMENT '结算经办人',
  settledate DATE COMMENT '结算日期',
  finalcost DECIMAL(14,2) CHECK (finalcost >= 0) COMMENT '入账/终审金额',
  finalperson CHAR(20) COMMENT '入账/终审人',
  finaldate DATE COMMENT '入账/终审日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成本总表';

-- 用户动态表
DROP TABLE IF EXISTS tb_dynamic;
CREATE TABLE tb_dynamic (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  content TEXT NOT NULL COMMENT '动态内容',
  images VARCHAR(2000) COMMENT '图片路径,逗号分隔',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户动态表';

-- 插入默认数据

-- 默认管理员账号 (密码: admin123)
INSERT INTO tb_user (username, password, role, status) VALUES
('admin', 'admin123', '管理员', 1),
('user1', 'user123', '普通用户', 1);

-- 示例油水井数据
INSERT INTO tb_well (well_name, well_type, well_status, field_name, layer, depth, operator, drilling_date, address) VALUES
('KQ-001', '油井', '生产', '克拉玛依油田', 'S1层', 1850.50, '张工', '2015-03-20', '克拉玛依区采油一厂'),
('KQ-002', '油井', '生产', '克拉玛依油田', 'S2层', 2100.00, '李工', '2016-07-15', '克拉玛依区采油一厂'),
('KS-001', '水井', '注水', '克拉玛依油田', 'S1层', 1780.00, '王工', '2017-01-10', '克拉玛依区采油二厂'),
('KQ-003', '油井', '关停', '克拉玛依油田', 'T1层', 2500.00, '赵工', '2014-11-05', '克拉玛依区采油一厂');
