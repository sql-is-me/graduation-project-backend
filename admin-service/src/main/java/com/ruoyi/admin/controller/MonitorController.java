package com.ruoyi.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.admin.service.Impl.MonitorServiceImpl;
import com.ruoyi.common.VO.OnlineUserInfo;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.entity.R;

/**
 * 管理员用户监控控制器（在线用户监控 + 管理员用户管理）
 */
@RestController
@RequestMapping("/admin/monitor")
public class MonitorController extends BaseController {

    @Autowired
    private MonitorServiceImpl monitorService;

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
    @DeleteMapping("/forceAdminLogout/{token}")
    public R<?> forceAdminLogout(@PathVariable String token) {
        monitorService.forceAdminLogout(token);
        return R.ok("强退成功");
    }

    /**
     * 强退在线用户
     */
    @DeleteMapping("/forceUserLogout/{token}")
    public R<?> forceUserLogout(@PathVariable String token) {
        monitorService.forceUserLogout(token);
        return R.ok("强退成功");
    }
}
