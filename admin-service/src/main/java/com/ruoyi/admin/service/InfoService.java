package com.ruoyi.admin.service;

import com.ruoyi.admin.dto.AdminInfoUpdateDTO;
import com.ruoyi.admin.dto.AdminPasswordUpdateDTO;
import com.ruoyi.common.entity.Admin;

public interface InfoService {
    public Admin getInfo();

    public void updateInfo(AdminInfoUpdateDTO dto);

    public void updatePassword(AdminPasswordUpdateDTO dto);

    public void updateAvatar(Long userId, String avatarUrl);
}
