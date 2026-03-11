package com.ruoyi.admin.mapper;

import java.util.List;

/**
 * 管理员菜单权限Mapper接口
 */
public interface MenuMapper {

    /**
     * 根据用户ID查询菜单权限标识
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    public List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 查询所有菜单权限标识
     *
     * @return 权限标识列表
     */
    public List<String> selectMenuPermsAll();
}
