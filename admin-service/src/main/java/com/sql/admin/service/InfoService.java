package com.sql.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.sql.common.entity.dto.AdminInfoUpdateDTO;
import com.sql.common.entity.dto.AdminPasswordUpdateDTO;
import com.sql.common.entity.dto.AdminUpdateEmailDTO;
import com.sql.common.entity.vo.AdminInfo;

public interface InfoService {
    /**
     * 获取管理员信息
     */
    public AdminInfo getInfo();

    /**
     * 更新管理员信息
     */
    public void updateInfo(AdminInfoUpdateDTO dto);

    /**
     * 更新管理员邮箱
     */
    public void updateAdminEmail(AdminUpdateEmailDTO dto);

    /**
     * 修改密码
     */
    public void updatePassword(AdminPasswordUpdateDTO dto);

    /**
     * 更换管理员头像
     */
    public void updateAvatar(MultipartFile mf);
}
