DROP TABLE IF EXISTS admins;
CREATE TABLE admins (
  admin_id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(100) DEFAULT '',
  nick_name VARCHAR(30) NOT NULL,
  email VARCHAR(50) DEFAULT '',
  phone VARCHAR(11) DEFAULT '',
  sex CHAR(1) DEFAULT '2',
  avatar VARCHAR(255) DEFAULT '',
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
(1, 'topadmin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'topadmin@example.com', '13900000001', '0', '', NULL, '0', NULL, '0', '2026-03-18 08:00:00', '2026-03-18 08:00:00'),
(2, 'store_admin_1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '店铺管理员1', 'store1@example.com', '13900000002', '0', '', 1, '1', 1, '0', '2026-03-18 08:05:00', '2026-03-18 08:05:00'),
(3, 'store_admin_2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '店铺管理员2', 'store2@example.com', '13900000003', '1', '', 2, '1', 1, '0', '2026-03-18 08:10:00', '2026-03-18 08:10:00');
