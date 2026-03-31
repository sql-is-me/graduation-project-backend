package com.sql.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.MonitorService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

/**
 * 管理员用户监控控制器（在线用户监控 + 管理员用户管理）
 * 仅系统管理员可访问（查看在线用户、强退用户）
 */
@RestController
@RequestMapping("/admin/monitor")
@LoginRequired
@RequiresType(UserTypes.ADMIN)
public class MonitorController extends BaseController {

    @Autowired
    private MonitorService monitorService;

    /**
     * 查询在线管理员列表
     * 支持分页、排序（默认按userId降序，可选按loginTime排序和asc升序）
     */
    @GetMapping("/online/list/admin")
    public R<?> listOnlineAdmins(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "userId") String orderByColumn,
            @RequestParam(defaultValue = "desc") String isAsc) {
        return R.ok(monitorService.getOnlineAdmins(pageNum, pageSize, orderByColumn, "asc".equalsIgnoreCase(isAsc)));
    }

    /**
     * 查询在线用户列表
     * 支持分页、排序（默认按userId降序，可选按loginTime排序和asc升序）
     */
    @GetMapping("/online/list/user")
    public R<?> listOnlineUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "userId") String orderByColumn,
            @RequestParam(defaultValue = "desc") String isAsc) {
        return R.ok(monitorService.getOnlineUsers(pageNum, pageSize, orderByColumn, "asc".equalsIgnoreCase(isAsc)));
    }

    /**
     * 强退在线管理员
     */
    @Log(title = "在线管理员", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/forceAdminLogout/{adminId}")
    public R<?> forceAdminLogout(@PathVariable String adminId) {
        monitorService.forceAdminLogout(adminId);
        return R.ok("强退成功");
    }

    /**
     * 强退在线用户
     */
    @Log(title = "在线用户", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/forceUserLogout/{userId}")
    public R<?> forceUserLogout(@PathVariable String userId) {
        monitorService.forceUserLogout(userId);
        return R.ok("强退成功");
    }
}
