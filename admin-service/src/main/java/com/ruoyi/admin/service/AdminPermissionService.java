package com.ruoyi.admin.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.admin.mapper.AdminMenuMapper;
import com.ruoyi.admin.mapper.AdminRoleMapper;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.system.api.domain.SysUser;

/**
 * 管理员权限服务（本地直查数据库）
 */
@Service
public class AdminPermissionService {

    @Autowired
    private AdminRoleMapper roleMapper;

    @Autowired
    private AdminMenuMapper menuMapper;

    /**
     * 获取角色权限标识
     */
    public Set<String> getRolePermission(SysUser user) {
        Set<String> roles = new HashSet<>();
        if (user.isAdmin()) {
            roles.add(Constants.SUPER_ADMIN);
        } else {
            List<String> roleKeys = roleMapper.selectRolePermissionByUserId(user.getUserId());
            roles.addAll(roleKeys);
        }
        return roles;
    }

    /**
     * 获取菜单权限标识
     */
    public Set<String> getMenuPermission(SysUser user) {
        Set<String> perms = new HashSet<>();
        if (user.isAdmin()) {
            perms.add(Constants.ALL_PERMISSION);
        } else {
            List<String> menuPerms = menuMapper.selectMenuPermsByUserId(user.getUserId());
            perms.addAll(menuPerms);
        }
        return perms;
    }
}
