package com.ruoyi.admin.mapper;

import java.util.List;
import com.ruoyi.system.api.domain.SysLogininfor;

/**
 * 登录日志Mapper接口
 */
public interface AdminLogininforMapper {

    /**
     * 查询登录日志列表
     *
     * @param logininfor 登录日志查询条件
     * @return 登录日志集合
     */
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);
}
