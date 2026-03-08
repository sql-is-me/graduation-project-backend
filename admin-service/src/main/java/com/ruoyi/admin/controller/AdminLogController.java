package com.ruoyi.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.admin.service.AdminMonitorService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresLogin;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.api.domain.SysLogininfor;
import com.ruoyi.system.api.domain.SysOperLog;

/**
 * 管理员日志控制器（操作日志 + 登录日志）
 */
@RestController
@RequestMapping("/admin/log")
public class AdminLogController extends BaseController {

    @Autowired
    private AdminMonitorService monitorService;

    // ==================== 操作日志 ====================

    /**
     * 查询操作日志列表
     */
    @RequiresLogin
    @RequiresPermissions("system:operlog:list")
    @GetMapping("/oper/list")
    public TableDataInfo listOperLog(SysOperLog operLog) {
        startPage();
        List<SysOperLog> list = monitorService.listOperLog(operLog);
        return getDataTable(list);
    }

    /**
     * 查询操作日志详情
     */
    @RequiresLogin
    @RequiresPermissions("system:operlog:query")
    @GetMapping("/oper/{operId}")
    public R<?> getOperLog(@PathVariable Long operId) {
        return R.ok(monitorService.getOperLogById(operId));
    }

    // ==================== 登录日志 ====================

    /**
     * 查询登录日志列表
     */
    @RequiresLogin
    @RequiresPermissions("system:logininfor:list")
    @GetMapping("/login/list")
    public TableDataInfo listLogininfor(SysLogininfor logininfor) {
        startPage();
        List<SysLogininfor> list = monitorService.listLogininfor(logininfor);
        return getDataTable(list);
    }

    /**
     * 解锁用户
     */
    @RequiresLogin
    @RequiresPermissions("system:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/login/unlock/{userName}")
    public R<?> unlock(@PathVariable("userName") String userName) {
        monitorService.unlockUser(userName);
        return R.ok("解锁成功");
    }
}
