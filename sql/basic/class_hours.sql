DROP TABLE IF EXISTS class_hours;
CREATE TABLE class_hours (
  ch_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  hours INT NOT NULL DEFAULT 0,
  used_hours INT NOT NULL DEFAULT 0,
  remaining_hours INT NOT NULL DEFAULT 0,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (ch_id),
  KEY idx_class_hour_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO class_hours (ch_id, user_id, hours, used_hours, remaining_hours, update_time) VALUES
(1, 1, 30, 6, 24, '2026-03-18 12:00:00'),
(2, 2, 20, 8, 12, '2026-03-18 12:05:00'),
(3, 3, 0, 0, 0, '2026-03-18 12:10:00');
