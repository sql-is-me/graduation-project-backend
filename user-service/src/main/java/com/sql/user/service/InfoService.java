package com.sql.user.service;

import org.springframework.web.multipart.MultipartFile;

import com.sql.common.entity.vo.ClassHourInfo;
import com.sql.common.entity.vo.UserInfo;
import com.sql.user.dto.UserInfoUpdateDTO;
import com.sql.user.dto.UserUpdateEmailDTO;

/**
 * 用户/教练个人信息服务
 */
public interface InfoService {
    /**
     * 获取当前登录用户信息
     */
    UserInfo getInfo();

    /**
     * 更新个人信息
     */
    void updateInfo(UserInfoUpdateDTO dto);

    /**
     * 修改邮箱
     */
    void updateEmail(UserUpdateEmailDTO dto);

    /**
     * 更换头像
     */
    void updateAvatar(MultipartFile mf);

    /**
     * 上传教练个人展示照片（仅coach）
     */
    void updatePhoto(MultipartFile mf);

    /**
     * 发送邮箱验证码
     */
    String sendEmailCode(String email);

    /**
     * 查询当前用户课时信息（总课时、已用、剩余）
     */
    ClassHourInfo getClassHour();
}
