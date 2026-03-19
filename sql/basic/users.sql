DROP TABLE IF EXISTS users;
CREATE TABLE users (
  user_id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(100) DEFAULT '',
  nick_name VARCHAR(30) NOT NULL,
  user_type CHAR(1) DEFAULT '0',
  email VARCHAR(50) DEFAULT '',
  phone VARCHAR(11) DEFAULT '',
  sex CHAR(1) DEFAULT '2',
  avatar VARCHAR(255) DEFAULT '',
  store_id BIGINT DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_users_username (username),
  KEY idx_user_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (user_id, username, password, nick_name, user_type, email, phone, sex, avatar, store_id, status, create_time, update_time) VALUES
(1, 'member_1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '会员张三', '0', 'member1@example.com', '13800000001', '0', '', 1, '0', '2026-03-18 09:00:00', '2026-03-18 09:00:00'),
(2, 'member_2', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '会员李四', '0', 'member2@example.com', '13800000002', '1', '', 1, '0', '2026-03-18 09:05:00', '2026-03-18 09:05:00'),
(3, 'coach_1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '教练王五', '1', 'coach1@example.com', '13700000001', '0', '', 1, '0', '2026-03-18 09:10:00', '2026-03-18 09:10:00');
