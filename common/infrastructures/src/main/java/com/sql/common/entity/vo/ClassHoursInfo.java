package com.sql.common.entity.vo;

import com.sql.common.entity.po.ClassHour;

import lombok.Data;

@Data
public class ClassHoursInfo {
    private Long chId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 总购买课时数
     */
    private Integer totalHours;

    /**
     * 已用课时数
     */
    private Integer usedHours;

    /**
     * 剩余课时数
     */
    private Integer remainingHours;

    public ClassHoursInfo(ClassHour classHour, String nickName) {
        this.chId = classHour.getChId();
        this.userId = classHour.getUserId();
        this.nickName = nickName;
        this.totalHours = classHour.getHours();
        this.usedHours = classHour.getUsedHours();
        this.remainingHours = classHour.getRemainingHours();
    }
}
