package com.sql.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Child;

/**
 * 孩子信息Mapper接口
 * 操作 children 表
 */
@Mapper
public interface ChildMapper extends BaseMapper<Child> {

    /**
     * 根据父母ID查询孩子列表
     */
    @Select("SELECT * FROM children WHERE parent_id = #{parentId} ORDER BY child_id ASC")
    List<Child> selectByParentId(@Param("parentId") Long parentId);
}
