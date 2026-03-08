package com.ruoyi.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.admin.dto.AdminPasswordUpdateDTO;
import com.ruoyi.admin.dto.AdminProfileUpdateDTO;
import com.ruoyi.admin.mapper.AdminUserMapper;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 管理员个人信息服务
 */
@Service
public class AdminProfileService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取管理员个人信息
     */
    public SysUser getProfile() {
        String username = SecurityUtils.getUsername();
        return adminUserMapper.selectUserByUserName(username);
    }

    /**
     * 修改管理员个人信息
     */
    public void updateProfile(AdminProfileUpdateDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getSysUser();

        currentUser.setNickName(dto.getNickName());
        currentUser.setEmail(dto.getEmail());
        currentUser.setPhonenumber(dto.getPhonenumber());
        currentUser.setSex(dto.getSex());

        // 校验手机号唯一
        if (StringUtils.isNotEmpty(dto.getPhonenumber())) {
            SysUser phoneUser = adminUserMapper.checkPhoneUnique(dto.getPhonenumber());
            if (phoneUser != null && !phoneUser.getUserId().equals(currentUser.getUserId())) {
                throw new ServiceException("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
            }
        }
        // 校验邮箱唯一
        if (StringUtils.isNotEmpty(dto.getEmail())) {
            SysUser emailUser = adminUserMapper.checkEmailUnique(dto.getEmail());
            if (emailUser != null && !emailUser.getUserId().equals(currentUser.getUserId())) {
                throw new ServiceException("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
            }
        }

        int rows = adminUserMapper.updateUserProfile(currentUser);
        if (rows <= 0) {
            throw new ServiceException("修改个人信息异常，请联系管理员");
        }
        // 更新缓存中的用户信息
        tokenService.setLoginUser(loginUser);
    }

    /**
     * 修改管理员密码
     */
    public void updatePassword(AdminPasswordUpdateDTO dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();

        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new ServiceException("旧密码和新密码不能为空");
        }

        String password = loginUser.getSysUser().getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            throw new ServiceException("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            throw new ServiceException("新密码不能与旧密码相同");
        }

        String encryptedPwd = SecurityUtils.encryptPassword(newPassword);
        int rows = adminUserMapper.resetUserPwd(loginUser.getUserid(), encryptedPwd);
        if (rows <= 0) {
            throw new ServiceException("修改密码异常，请联系管理员");
        }
        // 更新缓存
        loginUser.getSysUser().setPwdUpdateDate(DateUtils.getNowDate());
        loginUser.getSysUser().setPassword(encryptedPwd);
        tokenService.setLoginUser(loginUser);
    }

    /**
     * 修改管理员头像
     */
    public void updateAvatar(Long userId, String avatarUrl) {
        int rows = adminUserMapper.updateUserAvatar(userId, avatarUrl);
        if (rows <= 0) {
            throw new ServiceException("上传头像异常，请联系管理员");
        }
    }
}
