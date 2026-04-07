DROP TABLE IF EXISTS class_hours;
CREATE TABLE class_hours (
  ch_id           BIGINT   NOT NULL AUTO_INCREMENT   COMMENT '主键',
  user_id         BIGINT   NOT NULL                  COMMENT '会员ID（关联users表）',
  hours           INT      NOT NULL DEFAULT 0        COMMENT '累计购买课时数',
  used_hours      INT      NOT NULL DEFAULT 0        COMMENT '已用课时数',
  remaining_hours INT      NOT NULL DEFAULT 0        COMMENT '剩余课时数',
  create_time     DATETIME DEFAULT NULL              COMMENT '创建时间',
  update_time     DATETIME DEFAULT NULL              COMMENT '更新时间',
  PRIMARY KEY (ch_id),
  UNIQUE KEY uk_class_hour_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员课时表';

INSERT INTO class_hours (ch_id, user_id, hours, used_hours, remaining_hours, create_time, update_time) VALUES
(1, 1, 30, 6, 24, '2026-04-07 11:00:00', '2026-04-07 12:00:00'),
(2, 2, 20, 8, 12, '2026-04-07 11:05:00', '2026-04-07 12:05:00');
