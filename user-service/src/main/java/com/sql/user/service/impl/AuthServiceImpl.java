package com.sql.user.service.impl;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.api.RemoteLoginLogService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.UserOnline;
import com.sql.common.entity.db.LoginInfo;
import com.sql.common.entity.db.User;
import com.sql.common.enums.AccountStatus;
import com.sql.common.exception.ServiceException;
import com.sql.common.mail.service.MailService;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.UserTokenService;
import com.sql.user.dto.UserRegisterDTO;
import com.sql.user.dto.UserResetPasswordDTO;
import com.sql.user.mapper.UserMapper;
import com.sql.user.service.AuthService;
import com.sql.utils.IpUtils;
import com.sql.utils.PasswordUtils;
import com.sql.utils.StringUtils;

/**
 * 用户/教练登录与注册服务
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RemoteLoginLogService remoteLoginLogService;

    /**
     * 用户/教练登录
     */
    @Override
    public String login(String username, String password) {
        User user;
        try {
            if (password.length() < AuthConstants.PASSWORD_MIN_LENGTH
                    || password.length() > AuthConstants.PASSWORD_MAX_LENGTH) {
                throw new ServiceException("密码长度不符合要求");
            }
            if (username.length() < AuthConstants.USERNAME_MIN_LENGTH
                    || username.length() > AuthConstants.USERNAME_MAX_LENGTH) {
                throw new ServiceException("用户名长度不符合要求");
            }

            user = userMapper.selectByUsername(username);

            if (user == null) {
                throw new ServiceException("用户名或密码错误");
            }
            if (AccountStatus.DISABLE.getCode().equals(user.getStatus())) {
                throw new ServiceException("账号已停用，请联系管理员");
            }

            validatePassword(user, password);
        } catch (ServiceException e) {
            recordLoginInfo(username, AuthConstants.LOGIN_FAIL, e.getMessage());
            throw new ServiceException(e.getMessage());
        }

        userMapper.updateById(user);

        String accessToken = userTokenService.createToken(user);
        recordLoginInfo(user.getUsername(), AuthConstants.LOGIN_SUCCESS, "登录成功");

        return accessToken;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout(HttpServletRequest request) {
        UserOnline uo = userTokenService.getUO(userTokenService.getUOToken(request));
        String username = uo.getUserInfo().getUsername();

        // 删除用户缓存记录
        userTokenService.delUserOnline(userTokenService.getUOToken(request));

        recordLoginInfo(username, AuthConstants.LOGOUT, "退出成功");
    }

    /**
     * 用户/教练注册
     */
    @Override
    public void register(UserRegisterDTO registerDTO) {
        if (registerDTO == null) {
            throw new ServiceException("请求参数不能为空");
        }

        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String type = registerDTO.getType();

        if (StringUtils.isAnyBlank(username, password, type)) {
            throw new ServiceException("用户名/密码/类型必须填写");
        }
        if (username.length() < AuthConstants.USERNAME_MIN_LENGTH
                || username.length() > AuthConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < AuthConstants.PASSWORD_MIN_LENGTH
                || password.length() > AuthConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 校验用户名唯一性（同一张表）
        User existUser = userMapper.selectByUsername(username);
        if (existUser != null) {
            throw new ServiceException("账号'" + username + "'已存在");
        }

        // 教练注册需要店铺ID
        if ("1".equals(type) && registerDTO.getStoreId() == null) {
            throw new ServiceException("教练注册需要指定所属店铺");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtils.encryptPassword(password));
        user.setUserType(type);
        if ("1".equals(type)) {
            user.setStoreId(registerDTO.getStoreId());
        }

        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new ServiceException("注册失败，请联系管理员");
        }

        recordLoginInfo(username, AuthConstants.REGISTER, ("1".equals(type) ? "教练" : "会员") + "注册成功");
    }

    /**
     * 通过邮箱验证码重置密码
     */
    @Override
    public void resetPassword(UserResetPasswordDTO dto) {
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

        // 查询用户
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new ServiceException("该邮箱未绑定任何账号");
        }

        // 更新密码
        int rows = userMapper.updatePassword(user.getUserId(), PasswordUtils.encryptPassword(newPassword));
        if (rows <= 0) {
            throw new ServiceException("密码重置失败，请联系管理员");
        }

        // 清除密码错误次数缓存
        delWrongPWTimesCache(user.getUsername());
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
    public void validatePassword(User user, String password) {
        String username = user.getUsername();

        Integer retryCounter = redisService.getCacheObject(PasswordUtils.getWrongPWTimesKey(username));
        if (retryCounter == null) {
            retryCounter = 0;
        }

        if (retryCounter >= PasswordUtils.PASSWORD_MAX_RETRY_COUNT) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟",
                    PasswordUtils.PASSWORD_MAX_RETRY_COUNT, PasswordUtils.PASSWORD_LOCK_TIME);
            throw new ServiceException(errMsg);
        }

        if (!PasswordUtils.matchesPassword(password, user.getPassword())) {
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
     */
    private void delWrongPWTimesCache(String username) {
        String key = PasswordUtils.getWrongPWTimesKey(username);
        if (redisService.hasKey(key)) {
            redisService.deleteObject(key);
        }
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(String username, String status, String message) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setAccessTime(LocalDateTime.now());
        loginInfo.setUsername(username);
        loginInfo.setIpAddr(IpUtils.getIpAddr());
        loginInfo.setMsg(message);

        if (StringUtils.equalsAny(status, AuthConstants.LOGIN_SUCCESS, AuthConstants.LOGOUT, AuthConstants.REGISTER)) {
            loginInfo.setStatus(AuthConstants.LOGIN_SUCCESS_STATUS);
        } else if (AuthConstants.LOGIN_FAIL.equals(status)) {
            loginInfo.setStatus(AuthConstants.LOGIN_FAIL_STATUS);
        }
        remoteLoginLogService.saveLoginInfo(loginInfo, AuthConstants.INNER);
    }
}
