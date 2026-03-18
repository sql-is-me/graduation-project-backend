DROP TABLE IF EXISTS courses;
CREATE TABLE courses (
  course_id BIGINT NOT NULL AUTO_INCREMENT,
  creator_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  court_id BIGINT NOT NULL,
  course_date DATE NOT NULL,
  start_time DATETIME NOT NULL,
  total_hours INT NOT NULL,
  child_ids VARCHAR(255) DEFAULT '[]',
  coach_id BIGINT NOT NULL,
  status CHAR(1) DEFAULT '0',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (course_id),
  KEY idx_course_store_id (store_id),
  KEY idx_course_court_id (court_id),
  KEY idx_course_coach_id (coach_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO courses (course_id, creator_id, store_id, court_id, course_date, start_time, total_hours, child_ids, coach_id, status, create_time, update_time) VALUES
(1, 2, 1, 1, '2026-03-19', '2026-03-19 09:00:00', 2, '[1,2]', 3, '0', '2026-03-18 17:00:00', '2026-03-18 17:00:00'),
(2, 2, 1, 2, '2026-03-19', '2026-03-19 14:00:00', 1, '[3]', 3, '0', '2026-03-18 17:05:00', '2026-03-18 17:05:00'),
(3, 3, 2, 3, '2026-03-20', '2026-03-20 10:00:00', 3, '[1,3]', 3, '1', '2026-03-18 17:10:00', '2026-03-18 17:10:00');
