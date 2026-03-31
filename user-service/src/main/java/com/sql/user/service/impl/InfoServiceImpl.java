package com.sql.user.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sql.api.RemoteFileService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.User;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.UserInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.common.mail.service.MailService;
import com.sql.common.tokens.UserTokenService;
import com.sql.user.dto.UserInfoUpdateDTO;
import com.sql.user.dto.UserPasswordUpdateDTO;
import com.sql.user.dto.UserUpdateEmailDTO;
import com.sql.user.mapper.UserMapper;
import com.sql.user.service.InfoService;
import com.sql.utils.PasswordUtils;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileTypeUtils;
import com.sql.utils.file.MimeTypeUtils;

/**
 * 用户/教练个人信息服务
 */
@Service
public class InfoServiceImpl implements InfoService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RemoteFileService remoteFileService;

    @Value("${file.pic-base-url}")
    private String picBaseUrl;

    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserInfo getInfo() {
        User user = ContextHolder.getUO().getUserInfo();
        UserInfo info = new UserInfo(user);
        // 拼接完整头像 URL
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            info.setAvatar(picBaseUrl + user.getAvatar());
        }
        return info;
    }

    /**
     * 修改个人信息
     */
    @Override
    public void updateInfo(UserInfoUpdateDTO dto) {
        UserOnline uo = ContextHolder.getUO();
        User currentUser = uo.getUserInfo();

        if (dto.getNickName() != null) {
            currentUser.setNickName(dto.getNickName());
        }
        if (dto.getPhone() != null) {
            currentUser.setPhone(dto.getPhone());
        }
        if (dto.getSex() != null) {
            currentUser.setSex(dto.getSex());
        }

        // 校验手机号唯一
        if (StringUtils.isNotEmpty(dto.getPhone())) {
            User phoneOwner = userMapper.selectByPhone(dto.getPhone());
            if (phoneOwner != null && !phoneOwner.getUserId().equals(currentUser.getUserId())) {
                throw new ServiceException("修改用户'" + currentUser.getUsername() + "'失败，手机号码已存在");
            }
        }

        int rows = userMapper.updateById(currentUser);
        if (rows <= 0) {
            throw new ServiceException("修改个人信息异常，请联系管理员");
        }

        // 更新缓存中的用户信息
        userTokenService.refreshCacheInfo(uo);
    }

    /**
     * 修改邮箱
     */
    @Override
    public void updateEmail(UserUpdateEmailDTO dto) {
        UserOnline uo = ContextHolder.getUO();
        User currentUser = uo.getUserInfo();
        String email = dto.getEmail();

        // 校验新邮箱与旧邮箱不能相同
        if (currentUser.getEmail() != null) {
            mailService.sendWarningEmail(currentUser.getEmail());

            if (currentUser.getEmail().equals(email))
                throw new ServiceException("新邮箱不能与旧邮箱相同");
        }

        // 校验邮箱唯一
        User emailOwner = userMapper.selectByEmail(email);
        if (emailOwner != null && !emailOwner.getUserId().equals(currentUser.getUserId())) {
            throw new ServiceException("修改用户'" + currentUser.getUsername() + "'失败，邮箱账号已存在");
        }

        // 验证验证码是否有效
        try {
            mailService.verifyEmailCode(email, dto.getEmailCode());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

        currentUser.setEmail(email);
        int rows = userMapper.updateById(currentUser);
        if (rows <= 0) {
            throw new ServiceException("修改邮箱失败，请联系管理员");
        }

        // 更新缓存中的用户信息
        userTokenService.refreshCacheInfo(uo);
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(UserPasswordUpdateDTO dto) {
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();

        UserOnline uo = ContextHolder.getUO();
        User currentUser = uo.getUserInfo();

        if (!PasswordUtils.matchesPassword(oldPassword, currentUser.getPassword())) {
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
        int rows = userMapper.updatePassword(currentUser.getUserId(), encryptedPW);
        if (rows <= 0) {
            throw new ServiceException("更换密码失败，请联系管理员");
        }

        // 更新完用户密码后删除用户缓存记录
        userTokenService.delUserOnline(uo.getToken());
    }

    /**
     * 更换头像
     */
    @Override
    public void updateAvatar(MultipartFile mf) {
        if (mf.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }

        UserOnline uo = ContextHolder.getUO();
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
        if (StringUtils.isNotEmpty(uo.getUserInfo().getAvatar())) {
            remoteFileService.deletePicture(uo.getUserInfo().getAvatar());
        }

        int rows = userMapper.updateAvatar(uo.getUserInfo().getUserId(), fileUrl);
        if (rows <= 0) {
            throw new ServiceException("上传头像异常，请联系管理员");
        }

        // 更新缓存
        uo.getUserInfo().setAvatar(fileUrl);
        userTokenService.refreshCacheInfo(uo);
    }
}
