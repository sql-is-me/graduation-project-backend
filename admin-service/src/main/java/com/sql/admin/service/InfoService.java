package com.sql.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.sql.admin.dto.AdminInfoUpdateDTO;
import com.sql.admin.dto.AdminPasswordUpdateDTO;
import com.sql.common.entity.db.Admin;

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
