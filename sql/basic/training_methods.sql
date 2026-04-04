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
