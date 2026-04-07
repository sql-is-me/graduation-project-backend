package com.sql.common.entity.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课程-孩子关联记录
 * 记录每个孩子在每节课的出勤状态
 * <p>
 * status:
 * 0 - 待出勤
 * 1 - 正常完课
 * 2 - 迟到
 * 3 - 早退
 * 4 - 缺勤
 * 5 - 请假（审批通过后标记）
 * </p>
 */
@Data
@TableName("attendance_record")
public class AttendanceRecord {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 孩子ID
     */
    @TableField("child_id")
    private Long childId;

    /**
     * 出勤状态
     * 0-待出勤 1-正常完课 2-迟到 3-早退 4-缺勤 5-请假
     */
    private String status = "0";

    /**
     * 核销人ID（管理员）
     */
    @TableField("verify_admin_id")
    private Long verifyAdminId;

    /**
     * 核销时间
     */
    @TableField("verify_time")
    private LocalDateTime verifyTime;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
