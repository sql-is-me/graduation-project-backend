package com.ruoyi.admin.mapper;

import java.util.List;

/**
 * 管理员角色Mapper接口
 */
public interface AdminRoleMapper {

    /**
     * 根据用户ID查询角色权限标识
     *
     * @param userId 用户ID
     * @return 角色权限标识列表
     */
    public List<String> selectRolePermissionByUserId(Long userId);
}
