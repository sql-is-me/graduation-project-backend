package com.sql.admin.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.admin.mapper.AdminMapper;
import com.sql.admin.service.AuthService;
import com.sql.api.RemoteLoginLogService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.bo.AdminOnline;
import com.sql.common.entity.dto.AdminInviteDTO;
import com.sql.common.entity.dto.AdminRegisterDTO;
import com.sql.common.entity.dto.AdminResetPasswordDTO;
import com.sql.common.entity.po.Admin;
import com.sql.common.entity.po.LoginInfo;
import com.sql.common.enums.AccountStatus;
import com.sql.common.exception.ServiceException;
import com.sql.common.mail.service.MailService;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.AdminTokenService;
import com.sql.utils.IpUtils;
import com.sql.utils.PasswordUtils;
import com.sql.utils.StringUtils;

/**
 * 管理员登录与注册服务
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RemoteLoginLogService remoteLoginLogService;

    /**
     * 管理员登录
     */
    @Override
    public String login(String username, String password) {
        Admin admin;
        try {
            // 密码长度校验
            if (password.length() < AuthConstants.PASSWORD_MIN_LENGTH
                    || password.length() > AuthConstants.PASSWORD_MAX_LENGTH) {
                throw new ServiceException("密码长度不符合要求");
            }
            // 用户名长度校验
            if (username.length() < AuthConstants.USERNAME_MIN_LENGTH
                    || username.length() > AuthConstants.USERNAME_MAX_LENGTH) {
                throw new ServiceException("用户名长度不符合要求");
            }

            // 查询用户信息
            admin = adminMapper.selectByUsername(username);
            if (admin == null) {
                throw new ServiceException("用户名或密码错误");
            }
            if (AccountStatus.DISABLE.getCode().equals(admin.getStatus())) {
                throw new ServiceException("账号已停用，请联系管理员");
            }

            validatePassword(admin, password);
        } catch (ServiceException e) {
            recordLoginInfo(username, AuthConstants.LOGIN_FAIL, e.getMessage());
            throw new ServiceException(e.getMessage());
        }

        adminMapper.updateById(admin);

        String accessToken = adminTokenService.createToken(admin);
        recordLoginInfo(admin.getUsername(), AuthConstants.LOGIN_SUCCESS, "登录成功");

        return accessToken;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout(HttpServletRequest request) {
        AdminOnline ao = adminTokenService.getAO(adminTokenService.getAOToken(request));
        String username = ao.getAdminInfo().getUsername();

        // 删除用户缓存记录
        adminTokenService.delAdminCache(ao);

        recordLoginInfo(username, AuthConstants.LOGOUT, "退出成功");
    }

    /**
     * 管理员注册（需邀请码）
     */
    @Override
    public void register(AdminRegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        if (username.length() < AuthConstants.USERNAME_MIN_LENGTH
                || username.length() > AuthConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < AuthConstants.PASSWORD_MIN_LENGTH
                || password.length() > AuthConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 校验用户名唯一性
        Admin existAdmin = adminMapper.selectByUsername(username);
        if (existAdmin != null) {
            throw new ServiceException("账号'" + username + "'已存在");
        }

        // 校验邀请码
        String inviteCode = registerDTO.getInviteCode();
        String inviteKey = AuthConstants.INVITE_CODE + inviteCode;

        AdminInviteDTO inviteDTO = redisService.getCacheObject(inviteKey);
        if (inviteDTO == null) {
            throw new ServiceException("邀请码无效或已过期");
        }

        // 创建用户
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setNickName(username);
        admin.setPassword(PasswordUtils.encryptPassword(password));
        admin.setStoreId(inviteDTO.getStoreId());
        admin.setReferrerId(inviteDTO.getReferrerId());

        int rows = adminMapper.insert(admin);
        if (rows <= 0) {
            throw new ServiceException("注册失败，请联系管理员");
        }

        // 注册成功后删除邀请码（一次性使用）
        redisService.deleteObject(inviteKey);
        recordLoginInfo(username, AuthConstants.REGISTER, "管理员注册成功");
    }

    /**
     * 生成管理员邀请码
     */
    @Override
    public String generateInviteCode(HttpServletRequest request, Long storeId) {

        AdminOnline ao = adminTokenService.getAO(adminTokenService.getAOToken(request));

        if (ao == null || ao.getAdminInfo() == null) {
            throw new ServiceException("未登录或登录态失效");
        }

        Admin referrer = ao.getAdminInfo();
        if (referrer.isSysAdmin()) {
            // 系统管理员需指定门店ID
            if (storeId == null) {
                throw new ServiceException("系统管理员生成邀请码需指定门店ID");
            }
        } else {
            storeId = referrer.getStoreId();
            if (storeId == null) {
                throw new ServiceException("推荐人门店信息缺失");
            }
        }

        String inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String inviteKey = AuthConstants.INVITE_CODE + inviteCode;
        AdminInviteDTO inviteDTO = new AdminInviteDTO(referrer.getReferrerId(), storeId);

        redisService.setCacheObject(inviteKey, inviteDTO, AuthConstants.ADMIN_INVITE_EXPIRE, TimeUnit.MINUTES);

        return inviteCode;
    }

    /**
     * 通过邮箱验证码重置密码
     */
    @Override
    public void resetPassword(AdminResetPasswordDTO dto) {
        String email = dto.getEmail();
        String emailCode = dto.getEmailCode();
        String newPassword = dto.getNewPassword();

        // 校验邮箱验证码
        try {
            mailService.verifyEmailCode(email, emailCode);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

        // 密码长度校验
        if (newPassword.length() < AuthConstants.PASSWORD_MIN_LENGTH
                || newPassword.length() > AuthConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 查询管理员
        Admin admin = adminMapper.selectByEmail(email);
        if (admin == null) {
            throw new ServiceException("该邮箱未绑定任何管理员账号");
        }

        // 更新密码
        int rows = adminMapper.updatePassword(admin.getAdminId(), PasswordUtils.encryptPassword(newPassword));
        if (rows <= 0) {
            throw new ServiceException("密码重置失败，请联系管理员");
        }

        // 强制已登录的盗号者下线
        adminTokenService.checkAndDeleteCacheObject(admin.getAdminId());

        // 清除密码错误次数缓存
        delWrongPWTimesCache(admin.getUsername());
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

        return emailCode;// FIXME:自动化测试使用，正式环境不应返回验证码
    }

    /**
     * 密码校验
     */
    public void validatePassword(Admin admin, String password) {
        String username = admin.getUsername();

        Integer retryCounter = redisService.getCacheObject(PasswordUtils.getWrongPWTimesKey(username));
        if (retryCounter == null) {
            retryCounter = 0;
        }

        if (retryCounter >= PasswordUtils.PASSWORD_MAX_RETRY_COUNT) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", PasswordUtils.PASSWORD_MAX_RETRY_COUNT,
                    PasswordUtils.PASSWORD_LOCK_TIME);
            throw new ServiceException(errMsg);
        }

        if (!PasswordUtils.matchesPassword(password, admin.getPassword())) {
            retryCounter++;

            redisService.setCacheObject(PasswordUtils.getWrongPWTimesKey(username), retryCounter,
                    PasswordUtils.PASSWORD_LOCK_TIME, TimeUnit.MINUTES);
            throw new ServiceException("用户名或密码错误");
        } else {
            delWrongPWTimesCache(username);
        }
    }

    /**
     * 删除登录密码错误次数缓存
     *
     * @param username
     */
    private void delWrongPWTimesCache(String username) {
        String key = PasswordUtils.getWrongPWTimesKey(username);
        if (redisService.hasKey(key)) {
            redisService.deleteObject(key);
        }
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    public void recordLoginInfo(String username, String status, String message) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setAccessTime(LocalDateTime.now());
        loginInfo.setUsername(username);
        loginInfo.setIpAddr(IpUtils.getIpAddr());
        loginInfo.setMsg(message);

        // 日志状态
        if (StringUtils.equalsAny(status, AuthConstants.LOGIN_SUCCESS, AuthConstants.LOGOUT, AuthConstants.REGISTER)) {
            loginInfo.setStatus(AuthConstants.LOGIN_SUCCESS_STATUS);
        } else if (AuthConstants.LOGIN_FAIL.equals(status)) {
            loginInfo.setStatus(AuthConstants.LOGIN_FAIL_STATUS);
        }

        remoteLoginLogService.saveLoginInfo(loginInfo, AuthConstants.INNER);
    }
}
