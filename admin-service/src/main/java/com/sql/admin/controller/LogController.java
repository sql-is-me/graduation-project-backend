package com.sql.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.LogService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.dto.LoginLogSelectDTO;
import com.sql.common.entity.dto.OperLogSelectDTO;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.LoginInfo;
import com.sql.common.entity.vo.OperLogInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

/**
 * 系统管理员日志控制器（操作日志 + 登录日志）
 * 仅系统管理员可访问
 */
@RestController
@RequestMapping("/admin/log")
@LoginRequired
@RequiresType(UserTypes.ADMIN)
public class LogController extends BaseController {

    @Autowired
    private LogService logService;

    /**
     * 查询操作日志列表（支持按操作人、业务类型、操作状态筛选）
     */
    @PostMapping("/oper")
    public TableDataInfo listOperLog(@RequestBody OperLogSelectDTO dto) {
        startPage();
        List<OperLogInfo> list = logService.listOperLog(dto);
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
     * 批量删除操作日志
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/oper")
    public R<?> deleteOperLog(@RequestParam List<Long> operIds) {
        logService.deleteOperLog(operIds);
        return R.ok("删除操作日志成功");
    }

    /**
     * 清空操作日志
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/oper/clean")
    public R<?> cleanOperLog() {
        logService.cleanOperLog();
        return R.ok("清空操作日志成功");
    }

    /**
     * 查询登录日志列表（支持按用户名、IP地址、登录状态筛选）
     */
    @PostMapping("/login")
    public TableDataInfo listLoginLog(@RequestBody LoginLogSelectDTO dto) {
        startPage();
        List<LoginInfo> list = logService.listLoginLog(dto);
        return getDataTable(list);
    }

    /**
     * 查询登录日志详情
     */
    @GetMapping("/login/{loginLogId}")
    public R<?> getLoginLogById(@PathVariable Long loginLogId) {
        return R.ok(logService.getLoginLog(loginLogId));
    }

    /**
     * 批量删除登录日志
     */
    @Log(title = "登录日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/login")
    public R<?> deleteLoginLog(@RequestParam List<Long> logIds) {
        logService.deleteLoginLog(logIds);
        return R.ok("删除登录日志成功");
    }

    /**
     * 清空登录日志
     */
    @Log(title = "登录日志", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/login/clean")
    public R<?> cleanLoginLog() {
        logService.cleanLoginLog();
        return R.ok("清空登录日志成功");
    }
}
