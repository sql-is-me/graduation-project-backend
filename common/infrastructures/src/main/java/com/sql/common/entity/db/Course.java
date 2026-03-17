package com.sql.common.entity.db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sql.common.handler.LongListTypeHandler;
import lombok.Data;

@Data
@TableName(value = "courses", autoResultMap = true)
public class Course {
    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;

    /**
     * 创建人ID
     * 关联admins表
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 所属店铺ID
     * 关联stores表
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 场地ID
     * 关联场地表courts
     */
    @TableField("court_id")
    private Long courtId;

    /**
     * 课程日期
     */
    @TableField("course_date")
    private LocalDate courseDate;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 总课时数
     * 仅可取1-3
     */
    @TableField("total_hours")
    private Integer totalHours;

    /**
     * 上课孩子id列表
     * 关联children表
     */
    @TableField(value = "child_ids", typeHandler = LongListTypeHandler.class)
    private List<Long> childIds = new ArrayList<>();

    /**
     * 上课教练id
     * (关联users表)
     */
    @TableField("coach_id")
    private Long coachId;

    /**
     * 课程状态
     * 0-准备中，1-已完成
     */
    private String status = "0"; // 默认准备中

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
