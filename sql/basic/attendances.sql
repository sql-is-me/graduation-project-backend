-- 签到签退照片已合并到 courses 表中（sign_in_photo/sign_out_photo），此表已废弃。

-- 课程-孩子出勤记录表（对应 AttendanceRecord PO，MyBatis Plus 映射表名 attendance_records）
DROP TABLE IF EXISTS attendance_records;
CREATE TABLE attendance_records (
  record_id        BIGINT       NOT NULL AUTO_INCREMENT                      COMMENT '记录ID',
  course_id        BIGINT       NOT NULL                                     COMMENT '课程ID',
  child_id         BIGINT       NOT NULL                                     COMMENT '孩子ID',
  status           CHAR(1)      NOT NULL DEFAULT '0'                        COMMENT '出勤状态 0-待出勤 1-正常完课 2-迟到 3-早退 4-缺勤 5-请假',
  verify_admin_id  BIGINT       DEFAULT NULL                                 COMMENT '核销管理员ID',
  verify_time      DATETIME     DEFAULT NULL                                 COMMENT '核销时间',
  remark           VARCHAR(255) DEFAULT NULL                                 COMMENT '备注',
  create_time      DATETIME     DEFAULT NULL                                 COMMENT '创建时间',
  update_time      DATETIME     DEFAULT NULL                                 COMMENT '更新时间',
  PRIMARY KEY (record_id),
  UNIQUE KEY uk_course_child (course_id, child_id),
  KEY idx_child_id (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-孩子出勤记录表';

INSERT INTO attendance_records (record_id, course_id, child_id, status, verify_admin_id, verify_time, remark, create_time, update_time) VALUES
(1, 1, 1, '0', NULL, NULL, NULL, '2026-04-07 17:00:00', '2026-04-07 17:00:00'),
(2, 1, 2, '0', NULL, NULL, NULL, '2026-04-07 17:00:00', '2026-04-07 17:00:00'),
(3, 2, 3, '0', NULL, NULL, NULL, '2026-04-07 17:05:00', '2026-04-07 17:05:00'),
(4, 3, 1, '1', 3, '2026-04-11 14:00:00', NULL, '2026-04-07 17:10:00', '2026-04-11 14:00:00'),
(5, 3, 3, '3', 3, '2026-04-11 14:00:00', NULL, '2026-04-07 17:10:00', '2026-04-11 14:00:00');
