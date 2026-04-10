DROP DATABASE IF EXISTS `mydb`;

CREATE DATABASE `mydb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `mydb`;

-- ----------------------------
-- 1. 店铺表
-- ----------------------------
DROP TABLE IF EXISTS stores;
CREATE TABLE stores (
  store_id BIGINT NOT NULL AUTO_INCREMENT,
  creator_id BIGINT DEFAULT NULL,
  store_name VARCHAR(50) NOT NULL,
  address VARCHAR(255) DEFAULT NULL,
  owner_id BIGINT DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (store_id),
  KEY idx_store_creator_id (creator_id),
  KEY idx_store_owner_id (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO stores (store_id, creator_id, store_name, address, owner_id, status, create_time, update_time) VALUES
(1, 1, '1号排球训练馆', '中心路1号', 2, '0', '2026-03-18 07:00:00', '2026-03-18 07:00:00'),
(2, 1, '2号排球训练馆', '滨河大道88号', 3, '0', '2026-03-18 07:05:00', '2026-03-18 07:05:00'),
(3, 1, '3号排球训练馆', '东大街66号', 2, '1', '2026-03-18 07:10:00', '2026-03-18 07:10:00');

-- ----------------------------
-- 2. 管理员表
-- ----------------------------
DROP TABLE IF EXISTS admins;
CREATE TABLE admins (
  admin_id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(100) DEFAULT '',
  nick_name VARCHAR(30) NOT NULL,
  email VARCHAR(50) DEFAULT '',
  phone VARCHAR(11) DEFAULT '',
  sex CHAR(1) DEFAULT '2',
  avatar VARCHAR(255) DEFAULT '/default_admin.jpg',
  store_id BIGINT DEFAULT NULL,
  admin_type CHAR(1) DEFAULT '1',
  referrer_id BIGINT DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (admin_id),
  UNIQUE KEY uk_admins_username (username),
  KEY idx_admin_store_id (store_id),
  KEY idx_admin_referrer_id (referrer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO admins (admin_id, username, password, nick_name, email, phone, sex, avatar, store_id, admin_type, referrer_id, status, create_time, update_time) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '2244509212@qq.com', '13900000001', '0', '/default_admin.jpg', NULL, '0', NULL, '0', '2026-03-18 08:00:00', '2026-03-18 08:00:00'),
(2, 'manager1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '店铺管理员1', 'store1@example.com', '13900000002', '0', '/default_admin.jpg', 1, '1', 1, '0', '2026-03-18 08:05:00', '2026-03-18 08:05:00'),
(3, 'manager2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '店铺管理员2', 'store2@example.com', '13900000003', '1', '/default_admin.jpg', 2, '1', 1, '0', '2026-03-18 08:10:00', '2026-03-18 08:10:00');

-- ----------------------------
-- 3. 用户表（会员+教练）
-- ----------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  user_id BIGINT NOT NULL AUTO_INCREMENT,
  open_id VARCHAR(64) NOT NULL,
  union_id VARCHAR(64) DEFAULT NULL,
  nick_name VARCHAR(30) NOT NULL,
  user_type CHAR(1) DEFAULT '0',
  email VARCHAR(50) DEFAULT '',
  phone VARCHAR(11) DEFAULT '',
  sex CHAR(1) DEFAULT '2',
  avatar VARCHAR(255) DEFAULT '/default_user.jpg',
  photo VARCHAR(255) DEFAULT '',
  store_id BIGINT DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_users_open_id (open_id),
  KEY idx_user_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

Insert INTO users (user_id, open_id, union_id, nick_name, user_type, email, phone, sex, avatar, photo, store_id, status, create_time, update_time) VALUES
(1, 'openid_member_1', 'unionid_1', '会员小明', '0', 'member1@example.com', '13900000001', '0', '/default_user.jpg', '', 1, '0', '2026-03-18 09:00:00', '2026-03-18 09:00:00'),
(2, 'openid_coach_1', 'unionid_2', '教练小红', '1', 'coach1@example.com', '13900000002', '1', '/default_user.jpg', '/default_coach_photo.jpg', 1, '0', '2026-03-18 09:05:00', '2026-03-18 09:05:00');


-- ----------------------------
-- 4. 场地表
-- ----------------------------
DROP TABLE IF EXISTS courts;
CREATE TABLE courts (
  court_id BIGINT NOT NULL AUTO_INCREMENT,
  court_name VARCHAR(50) NOT NULL,
  store_id BIGINT NOT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (court_id),
  KEY idx_court_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO courts (court_id, court_name, store_id, status, create_time, update_time) VALUES
(1, 'A号场地', 1, '0', '2026-03-18 10:00:00', '2026-03-18 10:00:00'),
(2, 'B号场地', 1, '0', '2026-03-18 10:05:00', '2026-03-18 10:05:00'),
(3, 'C号场地', 2, '1', '2026-03-18 10:10:00', '2026-03-18 10:10:00');

-- ----------------------------
-- 5. 孩子表
-- ----------------------------
DROP TABLE IF EXISTS children;
CREATE TABLE children (
  child_id BIGINT NOT NULL AUTO_INCREMENT,
  parent_id BIGINT NOT NULL,
  child_name VARCHAR(50) NOT NULL,
  birthday DATE DEFAULT NULL,
  photo VARCHAR(255) DEFAULT '',
  sex CHAR(1) DEFAULT '2',
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (child_id),
  KEY idx_child_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO children (child_id, parent_id, child_name, birthday, photo, sex, status, create_time, update_time) VALUES
(1, 1, '小明', '2016-05-12', '/default_child_photo.jpg', '0', '0', '2026-03-18 11:00:00', '2026-03-18 11:00:00'),
(2, 1, '小红', '2017-08-21', '/default_child_photo.jpg', '1', '0', '2026-03-18 11:05:00', '2026-03-18 11:05:00');

-- ----------------------------
-- 6. 课时表
-- ----------------------------
DROP TABLE IF EXISTS class_hours;
CREATE TABLE class_hours (
  ch_id           BIGINT   NOT NULL AUTO_INCREMENT   COMMENT '主键',
  user_id         BIGINT   NOT NULL                  COMMENT '会员ID（关联users表）',
  hours           INT      NOT NULL DEFAULT 0        COMMENT '累计购买课时数',
  used_hours      INT      NOT NULL DEFAULT 0        COMMENT '已用课时数',
  remaining_hours INT      NOT NULL DEFAULT 0        COMMENT '剩余课时数',
  create_time     DATETIME DEFAULT NULL              COMMENT '创建时间',
  update_time     DATETIME DEFAULT NULL              COMMENT '更新时间',
  PRIMARY KEY (ch_id),
  UNIQUE KEY uk_class_hour_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员课时表';

INSERT INTO class_hours (ch_id, user_id, hours, used_hours, remaining_hours, create_time, update_time) VALUES
(1, 1, 30, 6, 24, '2026-04-07 11:00:00', '2026-04-07 12:00:00');

-- ----------------------------
-- 7. 优惠券表
-- ----------------------------
DROP TABLE IF EXISTS coupons;
CREATE TABLE coupons (
  coupon_id BIGINT NOT NULL AUTO_INCREMENT,
  coupon_name VARCHAR(100) NOT NULL,
  store_id BIGINT NOT NULL,
  creator_id BIGINT NOT NULL,
  coupon_type CHAR(1) NOT NULL,
  discount_value DECIMAL(10,2) NOT NULL,
  min_amount DECIMAL(10,2) DEFAULT 0.00,
  total_count INT NOT NULL,
  remaining_count INT NOT NULL,
  claim_limit INT NOT NULL DEFAULT 1,
  link_token VARCHAR(64) DEFAULT NULL COMMENT '活动链接领券Token，非空时可通过链接领取',
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (coupon_id),
  KEY idx_coupon_store_id (store_id),
  KEY idx_coupon_creator_id (creator_id),
  UNIQUE KEY uk_coupon_link_token (link_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO coupons (coupon_id, coupon_name, store_id, creator_id, coupon_type, discount_value, min_amount, total_count, remaining_count, claim_limit, start_time, end_time, status, create_time, update_time) VALUES
(1, '满300减50', 1, 2, '0', 50.00, 300.00, 100, 80, 1, '2026-03-01 00:00:00', '2026-04-01 23:59:59', '0', '2026-03-18 13:00:00', '2026-03-18 13:00:00'),
(2, '满100减20', 1, 2, '0', 20.00, 100.00, 200, 150, 2, '2026-03-01 00:00:00', '2026-04-01 23:59:59', '0', '2026-03-18 13:05:00', '2026-03-18 13:05:00'),
(3, '九折优惠券', 2, 3, '1', 9.00, 0.00, 120, 100, 1, '2026-03-05 00:00:00', '2026-04-05 23:59:59', '0', '2026-03-18 13:10:00', '2026-03-18 13:10:00');

-- ----------------------------
-- 8. 用户优惠券关联表
-- ----------------------------
DROP TABLE IF EXISTS user_coupons;
CREATE TABLE user_coupons (
  user_coupon_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  coupon_id BIGINT NOT NULL,
  status CHAR(1) NOT NULL DEFAULT '0',
  used_order_id BIGINT DEFAULT NULL,
  claim_time DATETIME DEFAULT NULL,
  used_time DATETIME DEFAULT NULL,
  PRIMARY KEY (user_coupon_id),
  UNIQUE KEY uk_user_coupon (user_id, coupon_id),
  KEY idx_uc_user_id (user_id),
  KEY idx_uc_coupon_id (coupon_id),
  KEY idx_uc_used_order_id (used_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO user_coupons (user_coupon_id, user_id, coupon_id, status, used_order_id, claim_time, used_time) VALUES
(1, 1, 1, '1', 1, '2026-03-18 14:00:00', '2026-03-18 16:00:00'),
(2, 1, 3, '2', NULL, '2026-03-18 14:10:00', NULL);

-- ----------------------------
-- 9. 订单表
-- ----------------------------
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  order_id BIGINT NOT NULL AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  user_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  product_type CHAR(1) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  discount_amount DECIMAL(10,2) DEFAULT 0.00,
  pay_amount DECIMAL(10,2) NOT NULL,
  coupon_id BIGINT DEFAULT NULL,
  status CHAR(1) NOT NULL,
  pay_type VARCHAR(20) DEFAULT NULL,
  pay_time DATETIME DEFAULT NULL,
  transaction_id VARCHAR(64) DEFAULT NULL,
  cancel_time DATETIME DEFAULT NULL,
  cancel_reason VARCHAR(255) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (order_id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_order_user_id (user_id),
  KEY idx_order_store_id (store_id),
  KEY idx_order_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO orders (order_id, order_no, user_id, store_id, product_type, quantity, unit_price, total_amount, discount_amount, pay_amount, coupon_id, status, pay_type, pay_time, transaction_id, cancel_time, cancel_reason, create_time, update_time) VALUES
(1, 'ORD202603180001', 1, 1, '0', 10, 100.00, 1000.00, 50.00, 950.00, 1, '1', 'wechat', '2026-03-18 16:00:00', 'WXTXN0001', NULL, NULL, '2026-03-18 15:55:00', '2026-03-18 16:00:00'),
(2, 'ORD202603180003', 1, 2, '0', 8, 120.00, 960.00, 0.00, 960.00, NULL, '2', NULL, NULL, NULL, '2026-03-18 16:30:00', '用户取消', '2026-03-18 16:10:00', '2026-03-18 16:30:00');

-- ----------------------------
-- 10. 课程表
-- ----------------------------
DROP TABLE IF EXISTS courses;
CREATE TABLE courses (
  course_id      BIGINT       NOT NULL AUTO_INCREMENT                        COMMENT '课程ID',
  creator_id     BIGINT       NOT NULL                                       COMMENT '创建人ID（关联admins表）',
  store_id       BIGINT       NOT NULL                                       COMMENT '所属店铺ID',
  court_id       BIGINT       NOT NULL                                       COMMENT '场地ID',
  course_date    DATE         NOT NULL                                       COMMENT '上课日期',
  start_time     DATETIME     NOT NULL                                       COMMENT '上课开始时间',
  total_hours    INT          NOT NULL                                       COMMENT '课程时长（课时数，1-3）',
  child_ids      VARCHAR(255) DEFAULT '[]'                                   COMMENT '上课孩子ID列表（JSON数组）',
  coach_id       BIGINT       DEFAULT NULL                                   COMMENT '教练ID（关联users表）',
  status         CHAR(1)      NOT NULL DEFAULT '0'                          COMMENT '课程状态 0-准备中 1-进行中 2-已完成 3-已取消',
  sign_in_photo  VARCHAR(512) DEFAULT NULL                                   COMMENT '签到照片URL（相对路径）',
  sign_in_time   DATETIME     DEFAULT NULL                                   COMMENT '签到时间',
  sign_out_photo VARCHAR(512) DEFAULT NULL                                   COMMENT '签退照片URL（相对路径）',
  sign_out_time  DATETIME     DEFAULT NULL                                   COMMENT '签退时间',
  verify_status  CHAR(1)      NOT NULL DEFAULT '0'                          COMMENT '核销状态 0-未核销 1-已核销',
  create_time    DATETIME     DEFAULT NULL                                   COMMENT '创建时间',
  update_time    DATETIME     DEFAULT NULL                                   COMMENT '更新时间',
  PRIMARY KEY (course_id),
  KEY idx_course_store_id (store_id),
  KEY idx_course_court_id (court_id),
  KEY idx_course_coach_id (coach_id),
  KEY idx_course_date     (course_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- ----------------------------
-- 11. 公告表
-- ----------------------------
DROP TABLE IF EXISTS notices;
CREATE TABLE notices (
  notice_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  content TEXT,
  status CHAR(1) DEFAULT '0',
  create_by VARCHAR(64) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_by VARCHAR(64) DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (notice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO notices (notice_id, title, content, status, create_by, create_time, update_by, update_time) VALUES
(1, '节假日休馆通知', '法定节假日期间本馆暂停营业，请各位会员知悉。', '0', 'admin', '2026-03-18 18:00:00', 'admin', '2026-03-18 18:00:00'),
(2, '春季优惠券上线', '春季优惠活动已开启，快来领取优惠券吧！', '0', 'manager1', '2026-03-18 18:05:00', 'manager1', '2026-03-18 18:05:00'),
(3, '系统维护公告', '本周末将进行系统升级维护，届时暂停服务。', '1', 'admin', '2026-03-18 18:10:00', 'admin', '2026-03-18 18:10:00');

-- ----------------------------
-- 12. 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS loginLog;
CREATE TABLE loginLog (
  log_id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  status CHAR(1) NOT NULL,
  ip_addr VARCHAR(128) DEFAULT NULL,
  msg VARCHAR(255) DEFAULT NULL,
  access_time DATETIME DEFAULT NULL,
  PRIMARY KEY (log_id),
  KEY idx_login_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 13. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS operLog;
CREATE TABLE operLog (
  oper_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) DEFAULT NULL,
  business_type INT DEFAULT 0,
  method VARCHAR(255) DEFAULT NULL,
  request_method VARCHAR(10) DEFAULT NULL,
  operator_type INT DEFAULT 0,
  operator_id BIGINT DEFAULT NULL,
  operator_name VARCHAR(64) DEFAULT NULL,
  oper_url VARCHAR(255) DEFAULT NULL,
  oper_ip VARCHAR(128) DEFAULT NULL,
  oper_param TEXT,
  json_result TEXT,
  status CHAR(1) NOT NULL,
  error_msg VARCHAR(2000) DEFAULT NULL,
  oper_time DATETIME DEFAULT NULL,
  cost_time BIGINT DEFAULT 0,
  PRIMARY KEY (oper_id),
  KEY idx_operator_id (operator_id),
  KEY idx_operator_name (operator_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 14. 教案表
-- ----------------------------
DROP TABLE IF EXISTS teaching_plans;
CREATE TABLE teaching_plans (
  tp_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  coach_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  file_url VARCHAR(255) NOT NULL,
  description VARCHAR(500) DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  reject_reason VARCHAR(255) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (tp_id),
  KEY idx_tp_coach_id (coach_id),
  KEY idx_tp_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 15. 训练方法表
-- ----------------------------
DROP TABLE IF EXISTS training_methods;
CREATE TABLE training_methods (
  tm_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  coach_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  file_url VARCHAR(255) NOT NULL,
  description VARCHAR(500) DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  reject_reason VARCHAR(255) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (tm_id),
  KEY idx_tm_coach_id (coach_id),
  KEY idx_tm_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 16. 请求/审批表
-- ----------------------------
DROP TABLE IF EXISTS requests;
CREATE TABLE requests (
  request_id BIGINT NOT NULL AUTO_INCREMENT,
  sender_id BIGINT NOT NULL COMMENT '发起人ID',
  sender_type CHAR(1) NOT NULL COMMENT '发起人类型 0会员 1教练',
  type VARCHAR(50) NOT NULL COMMENT '请求类型，见RequestConstants',
  payload JSON DEFAULT NULL COMMENT '业务载荷，按type解析',
  status CHAR(1) DEFAULT '0' COMMENT '整体状态 0待处理 1全部同意 2已拒绝',
  message VARCHAR(500) DEFAULT NULL COMMENT '申请说明',
  reject_reason VARCHAR(255) DEFAULT NULL COMMENT '拒绝原因',
  approver1_id BIGINT DEFAULT NULL COMMENT '审核人1 ID（店铺管理员，存storeId）',
  approver1_status CHAR(1) DEFAULT '0' COMMENT '审核人1状态 0待审 1同意 2拒绝',
  approver2_id BIGINT DEFAULT NULL COMMENT '审核人2 ID（系统管理员，null表示任意）',
  approver2_status CHAR(1) DEFAULT NULL COMMENT '审核人2状态',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (request_id),
  KEY idx_req_sender_id (sender_id),
  KEY idx_req_approver1 (approver1_id),
  KEY idx_req_type_status (type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 17. 课程-孩子出勤记录表
-- ----------------------------
DROP TABLE IF EXISTS attendance_records;
CREATE TABLE attendance_records (
  record_id        BIGINT       NOT NULL AUTO_INCREMENT                      COMMENT '记录ID',
  course_id        BIGINT       NOT NULL                                     COMMENT '课程ID',
  child_id         BIGINT       NOT NULL                                     COMMENT '孩子ID',
  status           CHAR(1)      NOT NULL DEFAULT '0'                        COMMENT '出勤状态 0-待出勤 1-正常完课 2-迟到 3-早退 4-缺勤 5-请假',
  verify_admin_id  BIGINT       DEFAULT NULL                                 COMMENT '核销管理员ID',
  verify_time      DATETIME     DEFAULT NULL                                 COMMENT '核销时间',
  remark           VARCHAR(255) DEFAULT NULL                                 COMMENT '备注',
  create_time      DATETIME     DEFAULT NULL                                 COMMENT '创建时间',
  update_time      DATETIME     DEFAULT NULL                                 COMMENT '更新时间',
  PRIMARY KEY (record_id),
  UNIQUE KEY uk_course_child (course_id, child_id),
  KEY idx_child_id (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-孩子出勤记录表';