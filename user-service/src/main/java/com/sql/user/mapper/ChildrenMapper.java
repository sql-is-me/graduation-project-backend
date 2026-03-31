package com.sql.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Children;

/**
 * 孩子信息Mapper接口
 * 操作 children 表
 */
@Mapper
public interface ChildrenMapper extends BaseMapper<Children> {

    /**
     * 根据父母ID查询孩子列表
     */
    default List<Children> selectByParentId(Long parentId) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Children>()
                .eq(Children::getParentId, parentId)
                .orderByAsc(Children::getChildId));
    }
}
