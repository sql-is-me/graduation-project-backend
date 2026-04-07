package com.sql.common.entity.vo;

import com.sql.common.entity.po.ClassHour;

import lombok.Data;

@Data
public class ClassHourInfo {
    /**
     * 课时数
     */
    private Integer hours;

    /**
     * 已用课时
     */
    private Integer usedHours;

    public ClassHourInfo(ClassHour classHour) {
        this.hours = classHour.getHours();
        this.usedHours = classHour.getUsedHours();
    }
}
