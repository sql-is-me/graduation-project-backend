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
  store_id BIGINT DEFAULT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_users_open_id (open_id),
  KEY idx_user_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
