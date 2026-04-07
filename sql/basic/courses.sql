DROP TABLE IF EXISTS courses;
CREATE TABLE courses (
  course_id    BIGINT       NOT NULL AUTO_INCREMENT                          COMMENT '课程ID',
  creator_id   BIGINT       NOT NULL                                         COMMENT '创建人ID（关联admins表）',
  store_id     BIGINT       NOT NULL                                         COMMENT '所属店铺ID',
  court_id     BIGINT       NOT NULL                                         COMMENT '场地ID',
  course_date  DATE         NOT NULL                                         COMMENT '上课日期',
  start_time   DATETIME     NOT NULL                                         COMMENT '上课开始时间',
  total_hours  INT          NOT NULL                                         COMMENT '课程时长（课时数，1-3）',
  child_ids    VARCHAR(255) DEFAULT '[]'                                     COMMENT '上课孩子ID列表（JSON数组）',
  coach_id     BIGINT       DEFAULT NULL                                     COMMENT '教练ID（关联users表）',
  status       CHAR(1)      NOT NULL DEFAULT '0'                            COMMENT '课程状态 0-准备中 1-进行中 2-已完成 3-已取消',
  sign_in_photo  VARCHAR(512) DEFAULT NULL                                   COMMENT '签到照片URL（相对路径）',
  sign_in_time   DATETIME     DEFAULT NULL                                   COMMENT '签到时间',
  sign_out_photo VARCHAR(512) DEFAULT NULL                                   COMMENT '签退照片URL（相对路径）',
  sign_out_time  DATETIME     DEFAULT NULL                                   COMMENT '签退时间',
  verify_status  CHAR(1)      NOT NULL DEFAULT '0'                          COMMENT '核销状态 0-未核销 1-已核销',
  create_time  DATETIME     DEFAULT NULL                                     COMMENT '创建时间',
  update_time  DATETIME     DEFAULT NULL                                     COMMENT '更新时间',
  PRIMARY KEY (course_id),
  KEY idx_course_store_id  (store_id),
  KEY idx_course_court_id  (court_id),
  KEY idx_course_coach_id  (coach_id),
  KEY idx_course_date      (course_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

INSERT INTO courses (course_id, creator_id, store_id, court_id, course_date, start_time, total_hours, child_ids, coach_id, status, sign_in_photo, sign_in_time, sign_out_photo, sign_out_time, verify_status, create_time, update_time) VALUES
(1, 2, 1, 1, '2026-04-10', '2026-04-10 09:00:00', 2, '[1,2]', 3, '0', NULL, NULL, NULL, NULL, '0', '2026-04-07 17:00:00', '2026-04-07 17:00:00'),
(2, 2, 1, 2, '2026-04-10', '2026-04-10 14:00:00', 1, '[3]',   3, '0', NULL, NULL, NULL, NULL, '0', '2026-04-07 17:05:00', '2026-04-07 17:05:00'),
(3, 3, 2, 3, '2026-04-11', '2026-04-11 10:00:00', 3, '[1,3]', 3, '2', '/pics/signs/sign_in_3.jpg', '2026-04-11 10:02:00', '/pics/signs/sign_out_3.jpg', '2026-04-11 13:05:00', '0', '2026-04-07 17:10:00', '2026-04-11 13:05:00');
