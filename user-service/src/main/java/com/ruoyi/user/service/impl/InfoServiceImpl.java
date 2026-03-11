package com.ruoyi.user.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.file.FileTypeUtils;
import com.ruoyi.common.core.utils.file.MimeTypeUtils;
import com.ruoyi.common.entity.File;
import com.ruoyi.common.entity.R;
import com.ruoyi.common.entity.User;
import com.ruoyi.common.entity.UserOnline;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.header.ContextHolder;
import com.ruoyi.common.tokens.UserTokenService;
import com.ruoyi.common.verifier.PWCheckUtils;
import com.ruoyi.system.api.RemoteFileService;
import com.ruoyi.user.dto.UserInfoUpdateDTO;
import com.ruoyi.user.dto.UserPasswordUpdateDTO;
import com.ruoyi.user.mapper.UserMapper;
import com.ruoyi.user.service.InfoService;

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
    private RemoteFileService remoteFileService;

    /**
     * 获取当前登录用户信息
     */
    @Override
    public User getInfo() {
        return ContextHolder.getUO().getUserInfo();
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
        if (dto.getEmail() != null) {
            currentUser.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            currentUser.setPhone(dto.getPhone());
        }
        if (dto.getSex() != null) {
            currentUser.setSex(dto.getSex());
        }

        // 校验手机号唯一
        if (StringUtils.isNotEmpty(dto.getPhone())) {
            User phoneOwner = userMapper.checkPhoneUnique(dto.getPhone());
            if (phoneOwner != null && !phoneOwner.getUserId().equals(currentUser.getUserId())) {
                throw new ServiceException("修改用户'" + currentUser.getUsername() + "'失败，手机号码已存在");
            }
        }

        // 校验邮箱唯一
        if (StringUtils.isNotEmpty(dto.getEmail())) {
            User emailOwner = userMapper.checkEmailUnique(dto.getEmail());
            if (emailOwner != null && !emailOwner.getUserId().equals(currentUser.getUserId())) {
                throw new ServiceException("修改用户'" + currentUser.getUsername() + "'失败，邮箱账号已存在");
            }
        }

        int rows = userMapper.updateById(currentUser);
        if (rows <= 0) {
            throw new ServiceException("修改个人信息异常，请联系管理员");
        }

        // 更新缓存中的用户信息
        userTokenService.refreshToken(uo);
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

        if (StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new ServiceException("旧密码和新密码不能为空");
        }
        if (!PWCheckUtils.matchesPassword(oldPassword, currentUser.getPassword())) {
            throw new ServiceException("修改密码失败，旧密码错误");
        }
        if (PWCheckUtils.matchesPassword(newPassword, currentUser.getPassword())) {
            throw new ServiceException("新密码不能与旧密码相同");
        }

        String encryptedPW = PWCheckUtils.encryptPassword(newPassword);

        int rows = userMapper.updatePassword(currentUser.getUserId(), encryptedPW);
        if (rows <= 0) {
            throw new ServiceException("修改密码异常，请联系管理员");
        }

        // 更新缓存
        uo.getUserInfo().setPassword(encryptedPW);
        userTokenService.refreshToken(uo);
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

        R<File> fileResult = remoteFileService.upload(mf);
        if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
            throw new ServiceException("文件服务异常，请联系管理员");
        }

        String fileUrl = fileResult.getData().getUrl();
        // 删除旧头像
        String oldAvatarUrl = uo.getUserInfo().getAvatar();
        if (StringUtils.isNotEmpty(oldAvatarUrl) && isNotDefaultUserAvatar(oldAvatarUrl)) {
            remoteFileService.delete(oldAvatarUrl);
        }

        User currentUser = uo.getUserInfo();

        int rows = userMapper.updateAvatar(currentUser.getUserId(), fileUrl);
        if (rows <= 0) {
            throw new ServiceException("上传头像异常，请联系管理员");
        }

        // 更新缓存
        uo.getUserInfo().setAvatar(fileUrl);
        userTokenService.refreshToken(uo);
    }

    private boolean isNotDefaultUserAvatar(String avatarUrl) {
        return !StringUtils.equals(avatarUrl, "/default-user-avatar.jpg");
    }
}
