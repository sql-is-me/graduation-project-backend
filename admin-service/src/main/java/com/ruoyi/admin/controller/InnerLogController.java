package com.ruoyi.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.admin.service.LogService;
import com.ruoyi.common.auth.annotation.InnerAuth;
import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.common.entity.result.R;
import com.ruoyi.utils.BaseController;

/**
 * 日志内部接口
 * 供内部服务调用，外部接口请使用
 */
@RestController
@RequestMapping
public class InnerLogController extends BaseController {

    @Autowired
    private LogService logService;

    @InnerAuth
    @PostMapping("/operLog")
    public R<Boolean> addOperLog(@RequestBody OperLog operLog) {
        int rows = logService.insertOperLog(operLog);
        if (rows > 0) {
            return R.ok(true);
        } else {
            return R.fail("插入操作日志失败");
        }
    }

    @InnerAuth
    @PostMapping("/loginInfo")
    public R<Boolean> addLoginInfo(@RequestBody LoginInfo loginInfo) {
        int rows = logService.insertLoginInfo(loginInfo);
        if (rows > 0) {
            return R.ok(true);
        } else {
            return R.fail("插入登录日志失败");
        }
    }
}
