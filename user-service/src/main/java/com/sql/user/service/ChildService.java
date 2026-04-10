package com.sql.user.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sql.common.entity.po.Child;
import com.sql.user.dto.ChildrenCreateDTO;
import com.sql.user.dto.ChildrenUpdateDTO;

/**
 * 孩子信息管理服务
 */
public interface ChildService {
    /**
     * 查询当前用户的孩子列表
     */
    List<Child> listByCurrentUser();

    /**
     * 根据ID查询孩子详情
     */
    Child getById(Long childId);

    /**
     * 新增孩子信息
     */
    void add(ChildrenCreateDTO dto);

    /**
     * 修改孩子信息
     */
    void update(Long childId, ChildrenUpdateDTO dto);

    /**
     * 上传/更换孩子照片
     */
    void updatePhoto(Long childId, MultipartFile childPhoto);

    /**
     * 删除孩子信息
     */
    void delete(Long childId);

}
