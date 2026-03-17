package com.sql.admin.service;

public interface ClassHourService {

    /**
     * 为用户增加课时
     *
     * @param userId 用户ID
     * @param hours  增加的课时数
     * @return 操作结果
     */
    int addClassHours(Long userId, int hours);
}