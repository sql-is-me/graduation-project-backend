package com.ruoyi.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.admin.service.LogService;
import com.ruoyi.common.auth.annotation.InnerAuth;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;

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
    public AjaxResult addOperLog(@RequestBody OperLog operLog) {
        return toAjax(logService.insertOperLog(operLog));
    }

    @InnerAuth
    @PostMapping("/loginInfo")
    public AjaxResult addLoginInfo(@RequestBody LoginInfo loginInfo) {
        return toAjax(logService.insertLoginInfo(loginInfo));
    }
}
