package com.ruoyi.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.admin.service.Impl.AdminMonitorService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresLogin;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.api.domain.SysUser;

/**
 * 管理员用户监控控制器（在线用户监控 + 管理员用户管理）
 */
@RestController
@RequestMapping("/admin/monitor")
public class AdminMonitorController extends BaseController {

    @Autowired
    private AdminMonitorService monitorService;

    // ==================== 在线用户监控 ====================

    /**
     * 查询在线用户列表
     */
    @RequiresLogin
    @RequiresPermissions("monitor:online:list")
    @GetMapping("/online/list")
    public R<?> listOnlineUsers(String ipaddr, String userName) {
        List<AdminMonitorService.OnlineUserInfo> list = monitorService.getOnlineUsers(ipaddr, userName);
        return R.ok(list);
    }

    /**
     * 强退在线用户
     */
    @RequiresLogin
    @RequiresPermissions("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/online/{tokenId}")
    public R<?> forceLogout(@PathVariable String tokenId) {
        monitorService.forceLogout(tokenId);
        return R.ok("强退成功");
    }

    // ==================== 管理员用户管理 ====================

    /**
     * 查询管理员用户列表（分页）
     */
    @RequiresLogin
    @RequiresPermissions("system:user:list")
    @GetMapping("/users")
    public TableDataInfo listAdminUsers(SysUser user) {
        startPage();
        List<SysUser> list = monitorService.listAdminUsers(user);
        return getDataTable(list);
    }
}
