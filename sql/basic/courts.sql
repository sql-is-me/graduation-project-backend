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
