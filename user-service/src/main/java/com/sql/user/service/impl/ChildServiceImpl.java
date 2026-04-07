package com.sql.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sql.api.RemoteFileService;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.Child;
import com.sql.common.entity.result.R;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.user.dto.ChildrenDTO;
import com.sql.user.mapper.ChildMapper;
import com.sql.user.service.ChildService;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileUtils;

/**
 * 孩子信息管理服务
 */
@Service
public class ChildServiceImpl implements ChildService {

    @Autowired
    private ChildMapper childMapper;

    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 查询当前会员的孩子列表
     */
    @Override
    public List<Child> listByCurrentUser() {
        UserOnline uo = ContextHolder.getUO();
        Long parentId = uo.getUserInfo().getUserId();
        List<Child> list = childMapper.selectByParentId(parentId);
        list.forEach(c -> c.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_CHILD_PHOTO, c.getPhoto())));
        return list;
    }

    /**
     * 根据ID查询孩子详情
     */
    @Override
    public Child getById(Long childId) {
        Child child = childMapper.selectById(childId);
        if (child == null) {
            throw new ServiceException("孩子信息不存在");
        }

        // 校验是否为当前用户的孩子
        UserOnline uo = ContextHolder.getUO();
        if (!child.getParentId().equals(uo.getUserInfo().getUserId())) {
            throw new ServiceException("无权查看该孩子信息");
        }
        child.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_CHILD_PHOTO, child.getPhoto()));
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

        Child child = new Child();
        child.setParentId(parentId);
        child.setChildName(dto.getChildName());
        child.setBirthday(dto.getBirthday());
        child.setPhoto(dto.getPhoto() != null ? dto.getPhoto() : "");
        child.setSex(dto.getSex() != null ? dto.getSex() : "0");
        child.setCreateTime(LocalDateTime.now());

        int rows = childMapper.insert(child);
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
        Child existChild = childMapper.selectById(dto.getChildId());
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
        existChild.setUpdateTime(LocalDateTime.now());

        int rows = childMapper.updateById(existChild);
        if (rows <= 0) {
            throw new ServiceException("修改孩子信息失败");
        }
    }

    /**
     * 上传/更换孩子照片
     */
    @Override
    public void updatePhoto(Long childId, MultipartFile file) {
        Child child = childMapper.selectById(childId);
        if (child == null) {
            throw new ServiceException("孩子信息不存在");
        }

        UserOnline uo = ContextHolder.getUO();
        if (!child.getParentId().equals(uo.getUserInfo().getUserId())) {
            throw new ServiceException("无权修改该孩子照片");
        }

        R<File> result = remoteFileService.uploadChildPhoto(file);
        if (StringUtils.isNull(result) || StringUtils.isNull(result.getData())) {
            throw new ServiceException("文件服务异常，请联系管理员");
        }

        // 删除旧照片（非默认）
        String oldPhoto = child.getPhoto();
        if (StringUtils.isNotEmpty(oldPhoto) && !oldPhoto.endsWith("/default_child.jpg")) {
            remoteFileService.deleteChildPhoto(oldPhoto);
        }

        child.setPhoto(result.getData().getUrl());
        childMapper.updateById(child);
    }

    /**
     * 删除孩子信息
     */
    @Override
    public void delete(Long childId) {
        Child existChild = childMapper.selectById(childId);
        if (existChild == null) {
            throw new ServiceException("孩子信息不存在");
        }

        UserOnline uo = ContextHolder.getUO();
        if (!existChild.getParentId().equals(uo.getUserInfo().getUserId())) {
            throw new ServiceException("无权删除该孩子信息");
        }

        int rows = childMapper.deleteById(childId);
        if (rows <= 0) {
            throw new ServiceException("删除孩子信息失败");
        }
    }
}
