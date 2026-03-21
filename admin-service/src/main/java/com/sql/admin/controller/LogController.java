package com.sql.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.dto.LoginInfoSelectDTO;
import com.sql.admin.dto.OperLogSelectDTO;
import com.sql.admin.service.LogService;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.db.LoginInfo;
import com.sql.common.entity.db.OperLog;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

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
    public TableDataInfo listOperLog(@Validated @RequestBody OperLogSelectDTO dto) {
        startPage();
        List<OperLog> list = logService.listOperLog(dto);
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
    @Log(title = "操作日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/oper/{operId}")
    public R<?> deleteOperLogById(@PathVariable Long operId) {
        return R.ok(logService.deleteOperLog(operId));
    }

    /**
     * 清空操作日志
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/oper/clean")
    public R<?> cleanOperLog() {
        logService.cleanOperLog();
        return R.ok();
    }

    /**
     * 查询登录日志列表（支持按用户名、IP地址、登录状态筛选）
     */
    @GetMapping("/login/list")
    public TableDataInfo listLogininfor(@Validated @RequestBody LoginInfoSelectDTO dto) {
        System.out.println("接收到查询登录日志列表的请求，查询条件：" + dto);
        startPage();
        List<LoginInfo> list = logService.listLoginInfo(dto);
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
    @Log(title = "登录日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/login/{loginInfoId}")
    public R<?> deleteLoginInfoById(@PathVariable Long loginInfoId) {
        return R.ok(logService.deleteLoginInfo(loginInfoId));
    }

    /**
     * 清空登录日志
     */
    @Log(title = "登录日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/login/clean")
    public R<?> cleanLoginInfo() {
        logService.cleanLoginInfo();
        return R.ok();
    }
}
