package com.sql.transaction.service;

import java.util.List;

import com.sql.common.entity.db.ClassHour;

public interface ClassHourService {

    /**
     * 为用户增加课时
     *
     * @param userId 用户ID
     * @param hours  增加的课时数
     * @return 操作结果
     */
    int addClassHours(Long userId, int hours);

    /**
     * 查看当前店铺旗下会员的课时余额
     */
    List<ClassHour> listClassHours();
}
