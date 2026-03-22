package com.sql.admin.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sql.admin.dto.AdminPasswordUpdateDTO;
import com.sql.admin.dto.AdminUpdateEmailDTO;
import com.sql.admin.dto.AdminInfoUpdateDTO;
import com.sql.admin.mapper.AdminMapper;
import com.sql.admin.service.InfoService;
import com.sql.api.RemoteFileService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.AdminOnline;
import com.sql.common.entity.File;
import com.sql.common.entity.db.Admin;
import com.sql.common.entity.result.R;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.common.mail.service.MailService;
import com.sql.common.tokens.AdminTokenService;
import com.sql.common.vo.AdminInfo;
import com.sql.utils.PasswordUtils;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileTypeUtils;
import com.sql.utils.file.MimeTypeUtils;

/**
 * 管理员个人信息服务
 */
@Service
public class InfoServiceImpl implements InfoService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RemoteFileService remoteFileService;

    @Value("${file.pic-base-url}")
    private String picBaseUrl;

    /**
     * 获取管理员个人信息
     */
    @Override
    public AdminInfo getInfo() {
        Admin admin = ContextHolder.getAO().getAdminInfo();
        AdminInfo info = new AdminInfo(admin);
        // 拼接完整头像 URL
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            info.setAvatar(picBaseUrl + admin.getAvatar());
        }
        return info;
    }

    /**
     * 修改管理员个人信息
     */
    @Override
    public void updateInfo(AdminInfoUpdateDTO dto) {
        AdminOnline ao = ContextHolder.getAO();
        Admin currentAdmin = ao.getAdminInfo();

        if (dto.getNickName() != null) {
            currentAdmin.setNickName(dto.getNickName());
        }
        if (dto.getPhone() != null) {
            currentAdmin.setPhone(dto.getPhone());
        }
        if (dto.getSex() != null) {
            currentAdmin.setSex(dto.getSex());
        }

        // 校验手机号唯一
        if (StringUtils.isNotEmpty(dto.getPhone())) {
            Admin phoneOwner = adminMapper.selectByPhone(dto.getPhone());
            if (phoneOwner != null && !phoneOwner.getAdminId().equals(currentAdmin.getAdminId())) {
                throw new ServiceException("修改用户'" + currentAdmin.getUsername() + "'失败，手机号码已存在");
            }
        }

        int rows = adminMapper.updateById(currentAdmin);
        if (rows <= 0) {
            throw new ServiceException("修改个人信息异常，请联系管理员");
        }

        // 更新缓存中的用户信息
        adminTokenService.refreshCacheInfo(ao);
    }

    /**
     * 修改管理员邮箱
     */
    @Override
    public void updateAdminEmail(AdminUpdateEmailDTO dto) {
        AdminOnline ao = ContextHolder.getAO();
        Admin currentAdmin = ao.getAdminInfo();
        String email = dto.getEmail();

        // 校验新邮箱与旧邮箱不能相同
        if (ao.getAdminInfo().getEmail() != null) {
            mailService.sendWarningEmail(ao.getAdminInfo().getEmail());

            if (ao.getAdminInfo().getEmail().equals(email))
                throw new ServiceException("新邮箱不能与旧邮箱相同");
        }

        // 校验邮箱唯一
        Admin emailOwner = adminMapper.selectByEmail(email);
        if (emailOwner != null && !emailOwner.getAdminId().equals(currentAdmin.getAdminId())) {
            throw new ServiceException("修改用户'" + currentAdmin.getUsername() + "'失败，邮箱账号已存在");
        }

        // 验证验证码是否有效
        try {
            mailService.verifyEmailCode(email, dto.getEmailCode());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

        currentAdmin.setEmail(email);
        int rows = adminMapper.updateById(currentAdmin);
        if (rows <= 0) {
            throw new ServiceException("修改邮箱失败，请联系管理员");
        }

        // 更新缓存中的用户信息
        adminTokenService.refreshCacheInfo(ao);
    }

    /**
     * 修改管理员密码
     */
    @Override
    public void updatePassword(AdminPasswordUpdateDTO dto) {
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();

        AdminOnline ao = ContextHolder.getAO();
        Admin currentAdmin = ao.getAdminInfo();

        if (!PasswordUtils.matchesPassword(oldPassword, currentAdmin.getPassword())) {
            throw new ServiceException("修改密码失败，旧密码错误");
        }

        if (PasswordUtils.isEqualPassword(oldPassword, newPassword)) {
            throw new ServiceException("新密码不能与旧密码相同");
        }

        // 密码长度校验
        if (newPassword.length() < AuthConstants.PASSWORD_MIN_LENGTH
                || newPassword.length() > AuthConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("新密码长度必须在5到20个字符之间");
        }

        String encryptedPW = PasswordUtils.encryptPassword(newPassword);
        int rows = adminMapper.updatePassword(currentAdmin.getAdminId(), encryptedPW);
        if (rows <= 0) {
            throw new ServiceException("更换密码失败，请联系管理员");
        }

        // 更新完用户密码后删除用户缓存记录
        adminTokenService.delAdminCache(ao);
    }

    /**
     * 修改管理员头像
     */
    @Override
    public void updateAvatar(MultipartFile mf) {
        if (mf.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }

        AdminOnline ao = ContextHolder.getAO();
        String extension = FileTypeUtils.getExtension(mf);
        if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
            throw new ServiceException("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
        }

        R<File> fileResult = remoteFileService.uploadPicture(mf);
        if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
            throw new ServiceException("文件服务异常，请联系管理员");
        }

        String fileUrl = fileResult.getData().getUrl();
        // 删除旧头像
        if (StringUtils.isNotEmpty(ao.getAdminInfo().getAvatar())) {
            remoteFileService.deletePicture(ao.getAdminInfo().getAvatar());
        }

        int rows = adminMapper.updateAvatar(ao.getAdminInfo().getAdminId(), fileUrl);
        if (rows <= 0) {
            throw new ServiceException("上传头像异常，请联系管理员");
        }

        // 更新缓存
        ao.getAdminInfo().setAvatar(fileUrl);
        adminTokenService.refreshCacheInfo(ao);
    }
}
