-- 创建数据库
DROP DATABASE IF EXISTS tb_school;
CREATE DATABASE tb_school;
-- 使用数据库
USE tb_school;

-- 操作日志表
CREATE TABLE IF NOT EXISTS operate_log(
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    operate_user INT UNSIGNED COMMENT '操作人ID',
    operate_time DATETIME COMMENT '操作时间',
    class_name VARCHAR(100) COMMENT '操作的类名',
    method_name VARCHAR(100) COMMENT '操作的方法名',
    method_params VARCHAR(1000) COMMENT '方法参数',
    return_value VARCHAR(2000) COMMENT '返回值',
    cost_time BIGINT COMMENT '方法执行耗时, 单位:ms'
) COMMENT '操作日志表';

-- 用户表
CREATE TABLE IF NOT EXISTS tb_user(
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名唯一',
    password VARCHAR(20) DEFAULT '123' NOT NULL COMMENT '密码不为空',
    gender VARCHAR(12) NULL COMMENT '性别',
    addr VARCHAR(30) COMMENT '地址',
    avatar LONGTEXT NULL COMMENT '头像',
    status INT(10) DEFAULT 1 NULL COMMENT '状态 1:正常 0:禁用',
    role VARCHAR(20) DEFAULT '志愿者' NOT NULL COMMENT '角色'
) COMMENT '用户表';

-- 插入用户数据
INSERT INTO tb_user (id, username, password, gender, addr, avatar, status, role) VALUES
(NULL, '系统', '0', '无', '无', 'http://localhost:8080/uploads/system-default.png', 1,'管理员'),
(NULL, 'admin', '123', '男', '北京', 'http://localhost:8080/uploads/1.jpg', 1,'管理员'),
(NULL, 'zhangsan', '123', '男', '北京', 'http://localhost:8080/uploads/2.jpg', 1,'志愿者'),
(NULL, 'lisi', '123', '男', '北京', 'http://localhost:8080/uploads/3.jpg', 1,'志愿者'),
(NULL, 'wangwu', '123', '男', '北京', 'http://localhost:8080/uploads/4.jpg', 1,'志愿者'),
(NULL, 'zhaoliu', '123', '男', '北京', 'http://localhost:8080/uploads/5.jpg', 1,'志愿者'),
(NULL, 'sunqi', '123', '男', '北京', 'http://localhost:8080/uploads/6.jpg', 1,'志愿者'),
(NULL, 'zhaojiu', '123', '男', '北京', 'http://localhost:8080/uploads/7.jpg', 1,'志愿者'),
(NULL, 'qianshi', '123', '男', '北京', 'http://localhost:8080/uploads/8.jpg', 1,'志愿者'),
(NULL, 'zhouba', '123', '男', '北京', 'http://localhost:8080/uploads/9.jpg', 1,'志愿者'),
(NULL, 'wujiu', '123', '男', '北京', 'http://localhost:8080/uploads/10.jpg', 1,'志愿者');

-- echarts统计数据表
CREATE TABLE IF NOT EXISTS tb_echarts (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    uname VARCHAR(200) NOT NULL COMMENT '活动名称',
    uvalue VARCHAR(300) NOT NULL COMMENT '数值数组',
    stats_type VARCHAR(50) NULL COMMENT '统计类型',
    activity_count INT DEFAULT 0 COMMENT '活动数量',
    volunteer_count INT DEFAULT 0 COMMENT '志愿者数量',
    participation_count INT DEFAULT 0 COMMENT '参与人次',
    supply_used_count INT DEFAULT 0 COMMENT '物资使用数量'
) COMMENT 'echarts统计数据表';

-- 插入echarts数据
INSERT INTO tb_echarts (uname, uvalue, stats_type, activity_count, volunteer_count, participation_count, supply_used_count) VALUES
('环保公益活动', '[580, 300, 150, 80, 200, 50,180]', '活动类型', 6, 36,8, 30),
('社区支教活动', '[320, 130, 250, 425, 215,80,380]', '物资类型', 0, 0, 0, 120),
('供免费辅导', '[170, 280, 150, 390, 210,370,270]', '活动时间', 0, 0, 0, 0),
('组织志愿者进行垃圾分类活动', '[520, 130, 350, 225, 315,116,380]', '物资时间', 0, 0, 0, 0),
('收集全校师生图书', '[436, 230, 140, 350, 160,270,350]', '志愿者时间', 0, 0, 0, 0),
('捐赠给贫困地区学校', '[320, 130, 450, 225, 115,150,390]', '活动类型', 0, 0, 0, 0);

-- 授权关系表
CREATE TABLE IF NOT EXISTS tb_authorization (
  user_id INT UNSIGNED,
  admin_id INT UNSIGNED,
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1-有效 0-禁用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, admin_id),
  FOREIGN KEY (user_id) REFERENCES tb_user(id),
  FOREIGN KEY (admin_id) REFERENCES tb_user(id)
) ENGINE=InnoDB COMMENT='最终授权关系表';

-- 插入授权数据
INSERT INTO tb_authorization VALUES (3, 2, 1, NOW(), NOW());
INSERT INTO tb_authorization VALUES (6, 2, 1, NOW(), NOW());
INSERT INTO tb_authorization VALUES (9, 2, 1, NOW(), NOW());
INSERT INTO tb_authorization VALUES (10, 2, 0, NOW(), NOW());

-- 权限申请记录表
CREATE TABLE IF NOT EXISTS tb_authorization_request (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id INT UNSIGNED NOT NULL,
  admin_id INT UNSIGNED NOT NULL,
  request_message VARCHAR(200) COMMENT '申请说明',
  status TINYINT NOT NULL DEFAULT -1 COMMENT '-1-待处理 1-已同意 0-已拒绝',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  processed_at TIMESTAMP NULL,
  FOREIGN KEY (user_id) REFERENCES tb_user(id),
  FOREIGN KEY (admin_id) REFERENCES tb_user(id),
  INDEX idx_status (status),
  INDEX idx_user (user_id)
) ENGINE=InnoDB COMMENT='权限申请记录表';

-- 插入权限申请数据
INSERT INTO tb_authorization_request (user_id, admin_id, request_message, status, created_at, processed_at) VALUES
(3, 2, "儿童走失紧急寻查", 1, NOW(), NOW()),
(6, 2, "夜间异常活动轨迹追踪", 1, NOW(), NOW()),
(7, 2, "游客财物遗失协查取证", 0, NOW(), NOW()),
(8, 2, "重大活动人流监控评估", 0, NOW(), NOW()),
(9, 2, "野生动物行为研究观测", 1, NOW(), NOW()),
(10, 2, "防汛期间危险区域监控", 1, NOW(), NOW());

INSERT INTO tb_authorization_request (user_id, admin_id, request_message, status, created_at) VALUES
(4, 2, "施工区域安全巡检核查", -1, NOW());

-- 文件元数据表
CREATE TABLE IF NOT EXISTS tb_file_metadata(
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  admin_id INT UNSIGNED,
  original_name VARCHAR(256),
  storage_path VARCHAR(1024),
  resource_type VARCHAR(64),
  description VARCHAR(1024),
  upload_time TIMESTAMP,
  status SMALLINT,
  FOREIGN KEY (admin_id) REFERENCES tb_user(id)
);

-- 消息表
CREATE TABLE IF NOT EXISTS tb_message (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  sender_id INT UNSIGNED NOT NULL,
  receiver_id INT UNSIGNED NOT NULL,
  sent_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  message VARCHAR(2048),
  checked SMALLINT DEFAULT 0
);

-- 插入消息数据
INSERT INTO tb_message (sender_id, receiver_id, message) VALUES
(2, 3, "请复核申请"),
(2, 3, "请再次复核申请"),
(1, 3, "管理员已确认您的申请");