package com.ruoyi.user.service;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.entity.User;
import com.ruoyi.user.dto.UserInfoUpdateDTO;
import com.ruoyi.user.dto.UserPasswordUpdateDTO;

/**
 * 用户/教练个人信息服务
 */
public interface InfoService {
    /**
     * 获取当前登录用户信息
     */
    User getInfo();

    /**
     * 更新个人信息
     */
    void updateInfo(UserInfoUpdateDTO dto);

    /**
     * 修改密码
     */
    void updatePassword(UserPasswordUpdateDTO dto);

    /**
     * 更换头像
     */
    void updateAvatar(MultipartFile mf);
}
