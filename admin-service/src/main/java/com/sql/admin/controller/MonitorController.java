package com.sql.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.MonitorService;
import com.sql.common.vo.OnlineUserInfo;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

/**
 * 管理员用户监控控制器（在线用户监控 + 管理员用户管理）
 * 仅顶级管理员可访问（查看在线用户、强退用户）
 */
@RestController
@RequestMapping("/admin/monitor")
@RequiresType(UserTypes.ADMIN)
public class MonitorController extends BaseController {

    @Autowired
    private MonitorService monitorService;

    /**
     * 查询在线管理员列表
     */
    @GetMapping("/online/list/admin")
    public R<?> listOnlineAdmins() {
        List<OnlineUserInfo> list = monitorService.getOnlineAdmins();
        return R.ok(list);
    }

    /**
     * 查询在线用户列表
     */
    @GetMapping("/online/list/user")
    public R<?> listOnlineUsers() {
        List<OnlineUserInfo> list = monitorService.getOnlineUsers();
        return R.ok(list);
    }

    /**
     * 强退在线管理员
     */
    @Log(title = "在线管理员", businessType = BusinessType.DELETE)
    @DeleteMapping("/forceAdminLogout/{token}")
    public R<?> forceAdminLogout(@PathVariable String token) {
        monitorService.forceAdminLogout(token);
        return R.ok("强退成功");
    }

    /**
     * 强退在线用户
     */
    @Log(title = "在线用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/forceUserLogout/{token}")
    public R<?> forceUserLogout(@PathVariable String token) {
        monitorService.forceUserLogout(token);
        return R.ok("强退成功");
    }
}
