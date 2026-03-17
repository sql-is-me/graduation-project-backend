package com.sql.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.ClassHourService;
import com.sql.common.auth.annotation.InnerAuth;
import com.sql.common.entity.result.R;

/**
 * 课时内部接口
 * 供内部服务调用（如transaction-service支付成功后增加课时）
 */
@RestController
@RequestMapping
public class InnerClassHourController {

    @Autowired
    private ClassHourService classHourService;

    /**
     * 增加用户课时
     */
    @InnerAuth
    @PostMapping("/classHour/innerAdd")
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
