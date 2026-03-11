package com.ruoyi.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.admin.dto.AdminInfoUpdateDTO;
import com.ruoyi.admin.dto.AdminPasswordUpdateDTO;
import com.ruoyi.common.entity.Admin;

public interface InfoService {
    /**
     * 获取管理员信息
     */
    public Admin getInfo();

    /**
     * 更新管理员信息
     */
    public void updateInfo(AdminInfoUpdateDTO dto);

    /**
     * 修改密码
     */
    public void updatePassword(AdminPasswordUpdateDTO dto);

    /**
     * 更换管理员头像
     */
    public void updateAvatar(MultipartFile mf);
}
