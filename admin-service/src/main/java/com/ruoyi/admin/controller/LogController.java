package com.ruoyi.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.admin.service.LogService;
import com.ruoyi.common.auth.annotation.RequiresType;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.common.entity.TableDataInfo;
import com.ruoyi.common.entity.result.R;
import com.ruoyi.common.enums.UserTypes;

/**
 * 顶级管理员日志控制器（操作日志 + 登录日志）
 * 仅顶级管理员可访问
 */
@RestController
@RequestMapping("/admin/log")
@RequiresType(UserTypes.ADMIN)
public class LogController extends BaseController {

    @Autowired
    private LogService logService;

    /**
     * 查询操作日志列表（支持按操作人、模块标题、业务类型、操作状态筛选）
     */
    @GetMapping("/oper/list")
    public TableDataInfo listOperLog(OperLog operLog) {
        startPage();
        List<OperLog> list = logService.listOperLog(operLog);
        return getDataTable(list);
    }

    /**
     * 查询操作日志详情
     */
    @GetMapping("/oper/{operId}")
    public R<?> getOperLogById(@PathVariable Long operId) {
        return R.ok(logService.getOperLog(operId));
    }

    /**
     * 删除操作日志
     */
    @DeleteMapping("/oper/{operId}")
    public R<?> deleteOperLogById(@PathVariable Long operId) {
        return R.ok(logService.deleteOperLog(operId));
    }

    /**
     * 查询登录日志列表（支持按用户名、IP地址、登录状态筛选）
     */
    @GetMapping("/login/list")
    public TableDataInfo listLogininfor(LoginInfo loginInfo) {
        startPage();
        List<LoginInfo> list = logService.listLoginInfo(loginInfo);
        return getDataTable(list);
    }

    /**
     * 查询登录日志详情
     */
    @GetMapping("/login/{loginInfoId}")
    public R<?> getLoginInfoById(@PathVariable Long loginInfoId) {
        return R.ok(logService.getLoginInfo(loginInfoId));
    }

    /**
     * 删除登录日志
     */
    @DeleteMapping("/login/{loginInfoId}")
    public R<?> deleteLoginInfoById(@PathVariable Long loginInfoId) {
        return R.ok(logService.deleteLoginInfo(loginInfoId));
    }
}
