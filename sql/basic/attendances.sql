-- 课程考勤记录（课程级别，一个课程一条签到签退记录）
CREATE TABLE IF NOT EXISTS `attendances` (
    `attendance_id`  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '考勤记录ID',
    `course_id`      BIGINT       NOT NULL                COMMENT '课程ID（唯一）',
    `sign_in_photo`  VARCHAR(512) DEFAULT NULL             COMMENT '签到照片URL（相对路径）',
    `sign_in_time`   DATETIME     DEFAULT NULL             COMMENT '签到时间',
    `sign_out_photo` VARCHAR(512) DEFAULT NULL             COMMENT '签退照片URL（相对路径）',
    `sign_out_time`  DATETIME     DEFAULT NULL             COMMENT '签退时间',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`attendance_id`),
    UNIQUE KEY `uk_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程考勤记录表';

-- 课程-孩子关联记录（每个孩子在每节课的出勤状态）
CREATE TABLE IF NOT EXISTS `course_children` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `course_id`       BIGINT       NOT NULL                COMMENT '课程ID',
    `child_id`        BIGINT       NOT NULL                COMMENT '孩子ID',
    `status`          CHAR(1)      NOT NULL DEFAULT '0'     COMMENT '出勤状态 0-待出勤 1-正常完课 2-早退 3-缺勤 4-请假',
    `verify_admin_id` BIGINT       DEFAULT NULL             COMMENT '核销管理员ID',
    `verify_time`     DATETIME     DEFAULT NULL             COMMENT '核销时间',
    `remark`          VARCHAR(255) DEFAULT NULL             COMMENT '备注',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_child` (`course_id`, `child_id`),
    KEY `idx_child_id` (`child_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-孩子出勤记录表';
