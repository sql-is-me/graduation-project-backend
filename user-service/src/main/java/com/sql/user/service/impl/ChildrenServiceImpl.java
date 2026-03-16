package com.sql.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.common.entity.Children;
import com.sql.common.entity.UserOnline;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.user.dto.ChildrenDTO;
import com.sql.user.mapper.ChildrenMapper;
import com.sql.user.service.ChildrenService;
import com.sql.utils.DateUtils;

/**
 * 孩子信息管理服务
 */
@Service
public class ChildrenServiceImpl implements ChildrenService {

    @Autowired
    private ChildrenMapper childrenMapper;

    /**
     * 查询当前用户的孩子列表
     */
    @Override
    public List<Children> listByCurrentUser() {
        UserOnline uo = ContextHolder.getUO();
        Long parentId = uo.getUserInfo().getUserId();
        return childrenMapper.selectByParentId(parentId);
    }

    /**
     * 根据ID查询孩子详情
     */
    @Override
    public Children getById(Long childId) {
        Children child = childrenMapper.selectById(childId);
        if (child == null) {
            throw new ServiceException("孩子信息不存在");
        }

        // 校验是否为当前用户的孩子
        UserOnline uo = ContextHolder.getUO();
        if (!child.getParentId().equals(uo.getUserInfo().getUserId())) {
            throw new ServiceException("无权查看该孩子信息");
        }
        return child;
    }

    /**
     * 新增孩子信息
     */
    @Override
    public void add(ChildrenDTO dto) {
        if (dto.getChildName() == null || dto.getChildName().isEmpty()) {
            throw new ServiceException("孩子姓名不能为空");
        }

        UserOnline uo = ContextHolder.getUO();
        Long parentId = uo.getUserInfo().getUserId();

        Children child = new Children();
        child.setParentId(parentId);
        child.setChildName(dto.getChildName());
        child.setBirthday(dto.getBirthday());
        child.setPhoto(dto.getPhoto() != null ? dto.getPhoto() : "");
        child.setSex(dto.getSex() != null ? dto.getSex() : "0");
        child.setCreateTime(DateUtils.getNowDate());

        int rows = childrenMapper.insert(child);
        if (rows <= 0) {
            throw new ServiceException("新增孩子信息失败");
        }
    }

    /**
     * 修改孩子信息
     */
    @Override
    public void update(ChildrenDTO dto) {
        if (dto.getChildId() == null) {
            throw new ServiceException("孩子ID不能为空");
        }

        // 校验归属权
        Children existChild = childrenMapper.selectById(dto.getChildId());
        if (existChild == null) {
            throw new ServiceException("孩子信息不存在");
        }

        UserOnline uo = ContextHolder.getUO();
        if (!existChild.getParentId().equals(uo.getUserInfo().getUserId())) {
            throw new ServiceException("无权修改该孩子信息");
        }

        // 更新信息
        if (dto.getChildName() != null) {
            existChild.setChildName(dto.getChildName());
        }
        if (dto.getBirthday() != null) {
            existChild.setBirthday(dto.getBirthday());
        }
        if (dto.getPhoto() != null) {
            existChild.setPhoto(dto.getPhoto());
        }
        if (dto.getSex() != null) {
            existChild.setSex(dto.getSex());
        }
        existChild.setUpdateTime(DateUtils.getNowDate());

        int rows = childrenMapper.updateById(existChild);
        if (rows <= 0) {
            throw new ServiceException("修改孩子信息失败");
        }
    }

    /**
     * 删除孩子信息
     */
    @Override
    public void delete(Long childId) {
        Children existChild = childrenMapper.selectById(childId);
        if (existChild == null) {
            throw new ServiceException("孩子信息不存在");
        }

        UserOnline uo = ContextHolder.getUO();
        if (!existChild.getParentId().equals(uo.getUserInfo().getUserId())) {
            throw new ServiceException("无权删除该孩子信息");
        }

        int rows = childrenMapper.deleteById(childId);
        if (rows <= 0) {
            throw new ServiceException("删除孩子信息失败");
        }
    }
}
