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
(1, 1, '阳光羽毛球馆', '中心路1号', 2, '0', '2026-03-18 07:00:00', '2026-03-18 07:00:00'),
(2, 1, '河畔球场', '滨河大道88号', 3, '0', '2026-03-18 07:05:00', '2026-03-18 07:05:00'),
(3, 1, '城市运动馆', '东大街66号', 2, '1', '2026-03-18 07:10:00', '2026-03-18 07:10:00');
