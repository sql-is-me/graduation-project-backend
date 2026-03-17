package com.sql.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.ClassHourService;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;

/**
 * 课时管理接口
 * 供管理员调用
 */
@RestController
@RequestMapping
public class ClassHourController {

    @Autowired
    private ClassHourService classHourService;

    /**
     * 增加用户课时
     */
    @RequiresType(UserTypes.ADMIN)
    @PostMapping("/classHour/add")
    public R<Boolean> addClassHours(@RequestParam("userId") Long userId,
            @RequestParam("hours") int hours) {
        int rows = classHourService.addClassHours(userId, hours);
        if (rows > 0) {
            return R.ok(true);
        } else {
            return R.fail("增加课时失败");
        }
    }
}
