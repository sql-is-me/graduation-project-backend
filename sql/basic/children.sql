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
(1, 1, '小明', '2016-05-12', '', '0', '0', '2026-03-18 11:00:00', '2026-03-18 11:00:00'),
(2, 1, '小红', '2017-08-21', '', '1', '0', '2026-03-18 11:05:00', '2026-03-18 11:05:00'),
(3, 2, '小刚', '2015-02-03', '', '0', '0', '2026-03-18 11:10:00', '2026-03-18 11:10:00');
