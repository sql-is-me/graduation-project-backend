package com.ruoyi.admin.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.admin.dto.AdminPasswordUpdateDTO;
import com.ruoyi.admin.dto.AdminInfoUpdateDTO;
import com.ruoyi.admin.mapper.AdminMapper;
import com.ruoyi.admin.service.InfoService;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.entity.Admin;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.header.ContextHolder;
import com.ruoyi.common.tokens.AdminTokenService;
import com.ruoyi.common.verifier.PWCheckUtils;

/**
 * 管理员个人信息服务
 */
@Service
public class InfoServiceImpl implements InfoService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminTokenService adminTokenService;

    /**
     * 获取管理员个人信息
     */
    public Admin getInfo() {
        return ContextHolder.getAO().getAdminInfo();
    }

    /**
     * 修改管理员个人信息
     */
    public void updateInfo(AdminInfoUpdateDTO dto) {
        AdminOnline ao = ContextHolder.getAO();
        Admin currentAdmin = ao.getAdminInfo();

        if (dto.getNickName() != null) {
            currentAdmin.setNickName(dto.getNickName());
        }
        if (dto.getEmail() != null) {
            currentAdmin.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            currentAdmin.setPhone(dto.getPhone());
        }
        if (dto.getSex() != null) {
            currentAdmin.setSex(dto.getSex());
        }

        // 校验手机号唯一
        if (StringUtils.isNotEmpty(dto.getPhone())) {
            Admin phoneOwner = adminMapper.checkPhoneUnique(dto.getPhone());
            if (phoneOwner != null && !phoneOwner.getAdminId().equals(currentAdmin.getAdminId())) {
                throw new ServiceException("修改用户'" + currentAdmin.getUsername() + "'失败，手机号码已存在");
            }
        }

        // 校验邮箱唯一
        if (StringUtils.isNotEmpty(dto.getEmail())) {
            Admin emailOwner = adminMapper.checkEmailUnique(dto.getEmail());
            if (emailOwner != null && !emailOwner.getAdminId().equals(currentAdmin.getAdminId())) {
                throw new ServiceException("修改用户'" + currentAdmin.getUsername() + "'失败，邮箱账号已存在");
            }
        }

        int rows = adminMapper.updateById(currentAdmin);
        if (rows <= 0) {
            throw new ServiceException("修改个人信息异常，请联系管理员");
        }

        // 更新缓存中的用户信息
        adminTokenService.refreshToken(ao);
    }

    /**
     * 修改管理员密码
     */
    public void updatePassword(AdminPasswordUpdateDTO dto) {
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();

        AdminOnline ao = ContextHolder.getAO();
        Admin currentAdmin = ao.getAdminInfo();

        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new ServiceException("旧密码和新密码不能为空");
        }
        if (PWCheckUtils.isEqualPassword(oldPassword, newPassword)) {
            throw new ServiceException("修改密码失败，旧密码错误");
        }

        if (PWCheckUtils.matchesPassword(newPassword, currentAdmin.getPassword())) {
            throw new ServiceException("新密码不能与旧密码相同");
        }

        String encryptedPW = PWCheckUtils.encryptPassword(newPassword);
        int rows = adminMapper.updatePassword(currentAdmin.getAdminId(), encryptedPW);
        if (rows <= 0) {
            throw new ServiceException("修改密码异常，请联系管理员");
        }

        // 更新缓存
        ao.getAdminInfo().setPassword(encryptedPW);
        adminTokenService.refreshToken(ao);
    }

    /**
     * 修改管理员头像
     */
    public void updateAvatar(Long adminId, String avatarUrl) {
        AdminOnline ao = ContextHolder.getAO();

        int rows = adminMapper.updateAvatar(adminId, avatarUrl);
        if (rows <= 0) {
            throw new ServiceException("上传头像异常，请联系管理员");
        }

        ao.getAdminInfo().setAvatar(avatarUrl);
        adminTokenService.refreshToken(ao);
    }
}
