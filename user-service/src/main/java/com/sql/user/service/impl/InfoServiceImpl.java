package com.sql.user.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sql.api.RemoteFileService;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.po.User;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.ClassHourInfo;
import com.sql.common.entity.vo.UserInfo;
import com.sql.user.mapper.ClassHourMapper;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.common.mail.service.MailService;
import com.sql.common.tokens.UserTokenService;
import com.sql.user.dto.UserInfoUpdateDTO;
import com.sql.user.dto.UserUpdateEmailDTO;
import com.sql.user.mapper.UserMapper;
import com.sql.user.service.InfoService;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileTypeUtils;
import com.sql.utils.file.FileUtils;
import com.sql.utils.file.MimeTypeUtils;

/**
 * 用户/教练个人信息服务
 */
@Service
public class InfoServiceImpl implements InfoService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClassHourMapper classHourMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserInfo getInfo() {
        User user = ContextHolder.getUO().getUserInfo();
        UserInfo info = new UserInfo(user);
        // 拼接完整头像 URL
        info.setAvatar(FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, user.getAvatar()));
        return info;
    }

    /**
     * 修改用户个人信息
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
                throw new ServiceException("修改用户'" + currentUser.getOpenId() + "'失败，手机号码已存在");
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
        if (StringUtils.isNotEmpty(currentUser.getEmail())) {
            if (currentUser.getEmail().equals(email))
                throw new ServiceException("新邮箱不能与旧邮箱相同");
            mailService.sendWarningEmail(currentUser.getEmail());
        }

        // 校验邮箱唯一
        User emailOwner = userMapper.selectByEmail(email);
        if (emailOwner != null && !emailOwner.getUserId().equals(currentUser.getUserId())) {
            throw new ServiceException("修改用户'" + currentUser.getOpenId() + "'失败，邮箱账号已存在");
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
     * 更换用户头像
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

        R<File> fileResult = remoteFileService.uploadAvatar(mf);
        if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
            throw new ServiceException("文件服务异常，请联系管理员");
        }

        String fileUrl = fileResult.getData().getUrl();
        // 删除旧头像
        if (StringUtils.isNotEmpty(uo.getUserInfo().getAvatar())) {
            remoteFileService.deleteAvatar(uo.getUserInfo().getAvatar());
        }

        int rows = userMapper.updateAvatar(uo.getUserInfo().getUserId(), fileUrl);
        if (rows <= 0) {
            throw new ServiceException("上传头像异常，请联系管理员");
        }

        // 更新缓存
        uo.getUserInfo().setAvatar(fileUrl);
        userTokenService.refreshCacheInfo(uo);
    }

    /**
     * 获取教练个人展示照片
     */
    @Override
    public String getCoachPhoto() {
        UserOnline uo = ContextHolder.getUO();
        User user = uo.getUserInfo();
        if (!"1".equals(user.getUserType())) {
            throw new ServiceException("该用户不是教练");
        }

        String photoUrl = user.getPhoto();
        if (StringUtils.isEmpty(photoUrl)) {
            throw new ServiceException("该教练暂无个人展示照片");
        }
        return FileUtils.toAbsoluteUrl(FileUtils.TYPE_COACH_PHOTO, photoUrl);
    }

    /**
     * 上传教练个人展示照片
     */
    @Override
    public void updatePhoto(MultipartFile mf) {
        if (mf.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }

        UserOnline uo = ContextHolder.getUO();
        String extension = FileTypeUtils.getExtension(mf);
        if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
            throw new ServiceException("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
        }

        R<File> fileResult = remoteFileService.uploadCoachPhoto(mf);
        if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
            throw new ServiceException("文件服务异常，请联系管理员");
        }

        String fileUrl = fileResult.getData().getUrl();
        // 原照片不是默认图时才删除
        String oldPhoto = uo.getUserInfo().getPhoto();
        if (StringUtils.isNotEmpty(oldPhoto)) {
            remoteFileService.deleteCoachPhoto(oldPhoto);
        }

        int rows = userMapper.updatePhoto(uo.getUserInfo().getUserId(), fileUrl);
        if (rows <= 0) {
            throw new ServiceException("上传照片异常，请联系管理员");
        }

        // 更新缓存
        uo.getUserInfo().setPhoto(fileUrl);
        userTokenService.refreshCacheInfo(uo);
    }

    /**
     * 查询当前用户课时信息
     */
    @Override
    public ClassHourInfo getClassHour() {
        Long userId = ContextHolder.getUO().getUserInfo().getUserId();
        ClassHour classHour = classHourMapper.getClassHourByUserId(userId);
        if (classHour == null) {
            throw new ServiceException("暂无课时记录");
        }
        ClassHourInfo classHourInfo = new ClassHourInfo(classHour);
        return classHourInfo;
    }

    /**
     * 发送邮箱验证码
     */
    @Override
    public String sendEmailCode(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new ServiceException("邮箱不能为空");
        }

        String emailCode = mailService.setEmailCode2Cache(email);
        mailService.sendEmailCode(email, emailCode);

        return emailCode; // FIXME:自动化测试使用，正式环境不应返回验证码
    }

}
