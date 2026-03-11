package com.ruoyi.user.service;

import java.util.List;

import com.ruoyi.common.entity.Children;
import com.ruoyi.user.dto.ChildrenDTO;

/**
 * 孩子信息管理服务
 */
public interface ChildrenService {
    /**
     * 查询当前用户的孩子列表
     */
    List<Children> listByCurrentUser();

    /**
     * 根据ID查询孩子详情
     */
    Children getById(Long childId);

    /**
     * 新增孩子信息
     */
    void add(ChildrenDTO dto);

    /**
     * 修改孩子信息
     */
    void update(ChildrenDTO dto);

    /**
     * 删除孩子信息
     */
    void delete(Long childId);
}
