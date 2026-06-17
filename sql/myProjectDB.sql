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
(1,1,'排球训练馆No.1','北京市海淀区排球路1号',2,'0','2026-03-18 07:00:00','2026-03-18 07:00:00'),
(2,1,'排球训练馆No.2','北京市朝阳区排球路666号',3,'0','2026-03-18 07:05:00','2026-03-18 07:05:00'),
(3,1,'排球训练馆No.3','北京市西城区排球路233号',4,'0','2026-03-18 07:10:00','2026-03-18 07:10:00'),
(4,1,'爱健身排球训练馆','上海市宝山区排球路1号',5,'0','2026-03-18 07:15:00','2026-03-18 07:15:00'),
(5,1,'爱锻炼排球训练馆','上海市静安区排球路1号',6,'0','2026-03-18 07:20:00','2026-03-18 07:20:00'),
(6,1,'加油排球训练馆','上海市嘉定区排球路1号',7,'0','2026-03-18 07:25:00','2026-03-18 07:25:00'),
(7,1,'排球小队排球训练馆','上海市浦东新区排球路1号',8,'0','2026-03-18 07:30:00','2026-03-18 07:30:00'),
(8,1,'二传手培训基地','上海市虹口区排球路1号',9,'1','2026-03-18 07:35:00','2026-03-18 07:35:00'),
(9,1,'发球排球培训基地','上海市普陀区排球路1号',9,'1','2026-03-18 07:40:00','2026-03-18 07:40:00'),
(10,1,'三传手排球培训基地','上海市金山区排球路1号',9,'1','2026-03-18 07:45:00','2026-03-18 07:45:00');

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
(1,'admin','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','系统管理员','2244509212@qq.com','18513993431','0','/default_admin.jpg',NULL,'0',NULL,'0','2026-03-18 08:00:00','2026-03-18 08:00:00'),
(2,'manager1','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','1号店铺店长','1store@example.com','18513993432','0','/default_admin.jpg',1,'1',1,'0','2026-03-18 08:05:00','2026-05-27 17:04:20'),
(3,'manager2','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','2号店铺店长','2store@example.com','18513993433','0','/default_admin.jpg',2,'1',1,'0','2026-03-18 08:10:00','2026-03-18 08:10:00'),
(4,'manager3','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','3号店铺店长','3store@example.com','18513993434','0','/default_admin.jpg',3,'1',1,'0','2026-03-18 08:15:00','2026-03-18 08:15:00'),
(5,'manager4','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','4号店铺店长','4store@example.com','18513993435','0','/default_admin.jpg',4,'1',1,'0','2026-03-18 08:20:00','2026-03-18 08:20:00'),
(6,'manager5','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','5号店铺店长','5store@example.com','18513993436','0','/default_admin.jpg',5,'1',1,'0','2026-03-18 08:25:00','2026-03-18 08:25:00'),
(7,'manager6','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','6号店铺店长','6store@example.com','18513993437','0','/default_admin.jpg',6,'1',1,'0','2026-03-18 08:30:00','2026-03-18 08:30:00'),
(8,'manager7','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','7号店铺店长','7store@example.com','18513993438','0','/default_admin.jpg',7,'1',1,'0','2026-03-18 08:35:00','2026-03-18 08:35:00'),
(9,'manager8','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','8号店铺店长','8store@example.com','18513993439','0','/default_admin.jpg',8,'1',1,'0','2026-03-18 08:40:00','2026-03-18 08:40:00'),
(10,'manager1-1','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','小郑','111@example.com','18513993431','0','/default_admin.jpg',1,'1',1,'0','2026-03-18 08:55:00','2026-03-18 08:55:00'),
(11,'manager1-2','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','小王','222@example.com','12345678902','0','/default_admin.jpg',1,'1',1,'0','2026-03-18 09:00:00','2026-03-18 09:00:00'),
(12,'manager1-3','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','小赵','333@example.com','12345678903','0','/default_admin.jpg',1,'1',1,'0','2026-03-18 09:05:00','2026-03-18 09:05:00');

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

INSERT INTO users (user_id, open_id, union_id, nick_name, user_type, email, phone, sex, avatar, photo, store_id, status, create_time, update_time) VALUES
(1,'openid_member_1','unionid_1','王小美','0','member1@example.com','13900000001','0','/default_user.jpg','',1,'0','2026-03-18 09:00:00','2026-03-18 09:00:00'),
(2,'openid_member_2','unionid_2','赵小光','0','member2@example.com','13900000002','0','/default_user.jpg','',1,'0','2026-03-18 09:00:00','2026-03-18 09:00:00'),
(3,'openid_member_3','unionid_3','宋小宝','0','member3@example.com','13900000003','0','/default_user.jpg','',1,'0','2026-03-18 09:00:00','2026-03-18 09:00:00'),
(4,'openid_member_4','unionid_4','李小丽','0','member4@example.com','13900000004','0','/default_user.jpg','',1,'0','2026-03-18 09:00:00','2026-03-18 09:00:00'),
(5,'openid_member_5','unionid_5','郭小贵','0','member5@example.com','13900000005','0','/default_user.jpg','',1,'0','2026-03-18 09:00:00','2026-03-18 09:00:00'),
(6,'openid_member_6','unionid_6','百小白','0','member6@example.com','13900000006','0','/default_user.jpg','',1,'1','2026-03-18 09:00:00','2026-03-18 09:00:00'),
(7,'o4MJR3cLrBQy1t1iz54wcWgPlmA8','unionid_7','王大钱','1','coach1@example.com','13900000007','1','/用户头像1.jpg','/教练照1.jpg',1,'0','2026-03-18 09:05:00','2026-03-18 09:05:00'),
(8,'openid_coach_2','unionid_8','宋大款','1','coach2@example.com','13900000008','1','/default_user.jpg','/教练照2.png',1,'0','2026-03-18 09:05:00','2026-03-18 09:05:00'),
(9,'openid_coach_3','unionid_9','赵大光','1','coach3@example.com','13900000009','1','/default_user.jpg','/教练照3.jpg',1,'0','2026-03-18 09:05:00','2026-03-18 09:05:00'),
(10,'o4MJR3ektKs9Vz-wCK6VVWYI7ZJg',NULL,'石小帅','0','2244509212@qq.com','18513993431','0','/用户头像2.jpg','',1,'0','2026-03-18 09:00:00','2026-03-18 09:00:00');


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
(1,'1号场地',1,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(2,'2号场地',1,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(3,'3号场地',1,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(4,'4号场地',1,'1','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(5,'1号场地',2,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(6,'2号场地',2,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(7,'西侧场地',3,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(8,'东侧场地',3,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(9,'主场地',4,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(10,'主场地',5,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(11,'主场地',6,'0','2026-03-18 10:00:00','2026-03-18 10:00:00'),
(12,'主场地',7,'0','2026-03-18 10:00:00','2026-03-18 10:00:00');

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
(1,1,'小明','2016-05-12','/孩子照1.jpg','1','0','2026-03-18 11:00:00','2026-03-18 11:00:00'),
(2,1,'小红','2017-05-16','/孩子照2.jpg','0','0','2026-03-18 11:00:00','2026-03-18 11:00:00'),
(3,2,'小李','2016-02-11','/孩子照1.jpg','1','0','2026-03-18 11:00:00','2026-03-18 11:00:00'),
(4,3,'小赵','2017-04-24','/孩子照4.jpg','1','0','2026-03-18 11:00:00','2026-03-18 11:00:00'),
(5,10,'小石','2016-04-24','/孩子照5.jpg','1','0','2026-03-18 11:00:00','2026-03-18 11:00:00'),
(6,10,'小绿','2017-09-11','/孩子照6.jpg','0','0','2026-03-18 11:00:00','2026-03-18 11:00:00');

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
(1,1,48,24,24,'2026-03-18 09:00:00','2026-03-19 09:00:00'),
(2,2,16,9,7,'2026-03-18 09:00:00','2026-03-19 09:00:00'),
(3,3,16,6,10,'2026-03-18 09:00:00','2026-03-19 09:00:00'),
(4,4,0,0,0,'2026-03-18 09:00:00','2026-03-19 09:00:00'),
(5,5,0,0,0,'2026-03-18 09:00:00','2026-03-19 09:00:00'),
(6,6,0,0,0,'2026-03-18 09:00:00','2026-03-19 09:00:00'),
(7,10,50,30,20,'2026-03-18 09:00:00','2026-05-27 18:35:42');

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

INSERT INTO coupons (coupon_id, coupon_name, store_id, creator_id, coupon_type, discount_value, min_amount, total_count, remaining_count, claim_limit, link_token, start_time, end_time, status, create_time, update_time) VALUES
(1,'满300 减50',1,2,'0',50.00,300.00,10,6,1,NULL,'2026-03-01 00:00:00','2026-04-01 23:59:59','1','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(2,'满1000 减100',1,2,'0',20.00,1000.00,100,97,2,NULL,'2026-03-01 00:00:00','2026-04-01 23:59:59','1','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(3,'满500 9折',1,3,'1',0.90,500.00,100,96,1,NULL,'2026-03-01 00:00:00','2026-04-01 23:59:59','1','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(4,'满2000 减300',1,2,'0',20.00,2000.00,100,96,1,NULL,'2026-03-01 00:00:00','2026-04-01 23:59:59','1','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(5,'满300 减50',1,2,'0',50.00,300.00,10,9,2,NULL,'2026-03-01 00:00:00','2026-05-01 00:00:00','0','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(6,'满100 减20',1,2,'0',20.00,100.00,10,8,1,NULL,'2026-03-01 00:00:00','2026-05-01 00:00:00','0','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(7,'满5000 8折',1,2,'1',0.80,5000.00,100,98,1,NULL,'2026-03-01 00:00:00','2026-05-01 00:00:00','0','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(8,'无门槛 9.5折',1,2,'1',0.95,0.00,10,3,1,NULL,'2026-03-01 00:00:00','2026-05-01 00:00:00','0','2026-02-28 12:00:00','2026-05-01 00:00:00'),
(9,'无门槛 减50',1,2,'0',50.00,0.00,10,1,1,NULL,'2026-03-01 00:00:00','2026-05-01 00:00:00','0','2026-02-28 12:00:00','2026-05-01 00:00:00');

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
  KEY idx_uc_user_coupon (user_id, coupon_id),
  KEY idx_uc_coupon_id (coupon_id),
  KEY idx_uc_used_order_id (used_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO user_coupons (user_coupon_id, user_id, coupon_id, status, used_order_id, claim_time, used_time) VALUES
(1,10,1,'2',NULL,'2026-04-15 00:00:00',NULL),
(2,10,4,'1',2,'2026-04-15 00:00:00','2026-04-24 00:00:00'),
(3,10,8,'2',NULL,'2026-04-15 00:00:00',NULL),
(4,10,9,'2',NULL,'2026-04-15 00:00:00',NULL);

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
(1,'LS1779877861219543',10,1,'1',30,50.00,1500.00,225.00,1275.00,NULL,'2','wechat',NULL,'wx_prepay_98ca77f8cea546ff','2026-05-27 18:39:17','','2026-04-24 00:00:00','2026-04-24 00:00:00'),
(2,'LS1779878137520209',10,1,'1',50,50.00,2500.00,800.00,1700.00,4,'1','wechat','2026-04-24 00:00:00','MOCK_177987814172923E830F0',NULL,NULL,'2026-04-24 00:00:00','2026-04-24 00:00:00'),
(3,'LS1779878348569548',10,1,'0',5,50.00,250.00,0.00,250.00,NULL,'0','wechat',NULL,'wx_prepay_e0f964a0c6094855',NULL,NULL,'2026-04-24 00:00:00','2026-04-24 00:00:00');

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

INSERT INTO courses (course_id, creator_id, store_id, court_id, course_date, start_time, total_hours, child_ids, coach_id, status, sign_in_photo, sign_in_time, sign_out_photo, sign_out_time, verify_status, create_time, update_time) VALUES
(1,10,1,1,'2026-04-11','2026-04-11 09:00:00',2,'[1,2,5,6]',7,'2','/集体照1.png','2026-04-11 09:00:00','/集体照2.jpg','2026-04-11 11:00:00','1','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(2,11,1,2,'2026-04-12','2026-04-12 09:00:00',2,'[1,2,5,6]',8,'2','/集体照3.jpg','2026-04-12 09:00:00','/集体照4.jpg','2026-04-12 11:00:00','1','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(3,12,1,3,'2026-04-12','2026-04-12 14:00:00',3,'[3,5]',9,'2','/集体照5.jpg','2026-04-12 14:00:00','/集体照1.jpg','2026-04-12 17:00:00','1','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(4,10,1,4,'2026-04-18','2026-04-18 14:00:00',2,'[1,2,5,6]',8,'2','/集体照4.jpg','2026-04-18 14:00:00','/集体照5.jpg','2026-04-18 16:00:00','1','2026-04-10 12:00:00','2026-05-27 18:10:53'),
(5,11,1,1,'2026-04-19','2026-04-19 09:00:00',2,'[1,2,5,6]',9,'2','/集体照2.jpg','2026-04-19 09:00:00','/集体照2.jpg','2026-04-19 11:00:00','0','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(6,12,1,2,'2026-04-19','2026-04-19 14:00:00',2,'[1,2,5,6]',7,'2','/集体照3.jpg','2026-04-19 14:00:00','/集体照1.png','2026-04-19 16:00:00','0','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(7,10,1,3,'2026-04-19','2026-04-19 14:00:00',1,'[3,4,6]',8,'2','/集体照1.png','2026-04-19 14:00:00','/集体照3.jpg','2026-04-19 15:00:00','0','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(8,11,1,4,'2026-04-25','2026-04-25 14:00:00',2,'[3,4]',7,'1','/集体照3.jpg','2026-04-25 14:00:00',NULL,NULL,'0','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(9,12,1,1,'2026-04-25','2026-04-25 18:00:00',2,'[1,2,5,6]',9,'1','/集体照5.jpg','2026-04-25 18:00:00',NULL,NULL,'0','2026-04-10 12:00:00','2026-04-10 12:00:00'),
(10,10,1,2,'2026-04-26','2026-04-26 09:00:00',3,'[3,4,5,6]',7,'0',NULL,NULL,NULL,NULL,'0','2026-04-10 12:00:00','2026-04-10 12:00:00');

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
(1,'系统维护公告','下周一23:00至第二日5:00将进行系统升级维护，届时暂停服务。','1','admin','2026-03-18 12:00:00','admin','2026-03-19 12:00:00'),
(2,'春季优惠券活动','春季优惠活动已开启，各店铺可以按需发放满1000减100优惠券。','0','admin','2026-03-20 12:00:00','admin','2026-03-20 12:00:00'),
(3,'五一劳动节活动','各单位可自行设计劳动节活动，需要提前上报总部审核。','0','admin','2026-04-24 12:00:00','admin','2026-04-24 12:00:00'),
(4,'系统异常波动','系统在五一劳动节期间收到了大量异常流量访问，服务出现异常波动，现已恢复。','0','admin','2026-05-02 12:00:00','admin','2026-05-02 12:00:00'),
(5,'系统维护公告','下周一23:00至第二日5:00将进行系统升级维护，届时暂停服务。','0','admin','2026-05-27 12:00:00','admin','2026-05-27 12:00:00');

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

INSERT INTO teaching_plans (tp_id, title, coach_id, store_id, file_url, description, status, reject_reason, create_time, update_time) VALUES
(1,'个人教学计划',7,1,'/教学计划.docx','个人所拥有的训练计划','0',NULL,'2026-04-14 14:00:00','2026-04-14 14:00:00');

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

INSERT INTO training_methods (tm_id, title, coach_id, store_id, file_url, description, status, reject_reason, create_time, update_time) VALUES
(1,'我的训练方法1',7,1,'/训练方法1.docx','这是训练方法1','0',NULL,'2026-04-14 12:00:00','2026-04-14 12:00:00'),
(2,'我的训练方法2',7,1,'/训练方法2.docx','这是训练方法2','2','内容不合规','2026-04-14 12:00:00','2026-04-14 12:00:00'),
(3,'别人的训练方法',8,1,'/别人的训练方法.docx','这是别人的训练方法','1',NULL,'2026-04-14 12:00:00','2026-04-14 12:00:00');

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

INSERT INTO requests (request_id, sender_id, sender_type, type, payload, status, message, reject_reason, approver1_id, approver1_status, approver2_id, approver2_status, create_time, update_time) VALUES
(1,10,'0','vip_leave','{"childId": 5, "courseId": 10}','1','孩子生病',NULL,1,'1',NULL,NULL,'2026-04-24 09:00:00','2026-04-25 09:00:00'),
(2,10,'0','vip_leave','{"childId": 6, "courseId": 10}','0','孩子被传染，也生病了！',NULL,1,'0',NULL,NULL,'2026-04-24 09:00:00','2026-04-24 09:00:00'),
(3,10,'0','vip_bind_store','{"targetStoreId": 2}','0','想换地方了',NULL,1,'0',NULL,'0','2026-04-25 09:00:00','2026-04-25 09:00:00'),
(4,7,'1','coach_bind_store','{"targetStoreId": 2}','0','那边老板给的待遇高',NULL,1,'0',NULL,'0','2026-04-25 09:00:00','2026-04-25 09:00:00'),
(5,7,'1','coach_upload_teaching_plan','{"tpId": 1, "fileUrl": "http://127.0.0.1:9900/tps/教学计划.docx"}','0','请审核我的教学计划',NULL,1,'0',NULL,NULL,'2026-04-25 09:00:00','2026-04-25 09:00:00'),
(6,7,'1','coach_upload_training_method','{"tmId": 1, "fileUrl": "http://127.0.0.1:9900/tms/训练方法1.docx"}','0','请审核我的训练方法1',NULL,1,'0',NULL,NULL,'2026-04-25 09:00:00','2026-04-25 09:00:00'),
(7,7,'1','coach_upload_training_method','{"tmId": 2, "fileUrl": "http://127.0.0.1:9900/tms/训练方法2.docx"}','2','请审核我的训练方法2','内容不合规',1,'2',NULL,NULL,'2026-04-25 09:00:00','2026-04-25 09:00:00'),
(8,7,'1','coach_upload_training_method','{"tmId": 4, "fileUrl": "http://127.0.0.1:9900/tms/不存在的文件.docx"}','0','请拒绝',NULL,1,'0',NULL,NULL,'2026-04-25 09:00:00','2026-04-25 09:00:00');

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

INSERT INTO attendance_records (record_id, course_id, child_id, status, verify_admin_id, verify_time, remark, create_time, update_time) VALUES
(1,1,1,'1',2,'2026-04-11 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-11 11:00:00'),
(2,1,2,'1',2,'2026-04-11 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-11 11:00:00'),
(3,1,5,'1',2,'2026-04-11 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-11 11:00:00'),
(4,1,6,'1',2,'2026-04-11 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-11 11:00:00'),
(5,2,1,'1',2,'2026-04-12 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-12 11:00:00'),
(6,2,2,'1',2,'2026-04-12 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-12 11:00:00'),
(7,2,5,'1',2,'2026-04-12 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-12 11:00:00'),
(8,2,6,'1',2,'2026-04-12 11:00:00',NULL,'2026-05-27 12:00:00','2026-04-12 11:00:00'),
(9,3,3,'1',2,'2026-04-12 17:00:00',NULL,'2026-05-27 12:00:00','2026-05-27 17:47:52'),
(10,3,5,'1',2,'2026-04-12 17:00:00',NULL,'2026-05-27 12:00:00','2026-05-27 17:47:52'),
(11,4,1,'1',2,'2026-05-27 18:10:53',NULL,'2026-05-27 12:00:00','2026-05-27 18:10:53'),
(12,4,2,'2',2,'2026-05-27 18:10:53',NULL,'2026-05-27 12:00:00','2026-05-27 18:10:53'),
(13,4,5,'3',2,'2026-05-27 18:10:53',NULL,'2026-05-27 12:00:00','2026-05-27 18:10:53'),
(14,4,6,'4',2,'2026-05-27 18:10:53',NULL,'2026-05-27 12:00:00','2026-05-27 18:10:53'),
(15,5,1,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(16,5,2,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(17,5,5,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(18,5,6,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(19,6,1,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(20,6,2,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(21,6,5,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(22,6,6,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(23,7,3,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(24,7,4,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(25,7,6,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(26,8,3,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(27,8,4,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(28,9,1,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(29,9,2,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(30,9,5,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(31,9,6,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(32,10,3,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(33,10,4,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00'),
(34,10,5,'5',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 18:09:58'),
(35,10,6,'0',NULL,NULL,NULL,'2026-05-27 12:00:00','2026-05-27 12:00:00');