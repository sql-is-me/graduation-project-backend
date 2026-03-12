package com.ruoyi.user.service.impl;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.DateUtils;
import com.ruoyi.common.IpUtils;
import com.ruoyi.common.StringUtils;
import com.ruoyi.common.Constants.AuthConstants;
import com.ruoyi.common.JWT.JWTService;
import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.User;
import com.ruoyi.common.enums.AccountStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.tokens.UserTokenService;
import com.ruoyi.common.verifier.PWCheckUtils;
import com.ruoyi.system.api.RemoteLogService;
import com.ruoyi.user.dto.UserRegisterDTO;
import com.ruoyi.user.mapper.UserMapper;
import com.ruoyi.user.service.AuthService;

/**
 * 用户/教练登录与注册服务
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 用户/教练登录
     */
    @Override
    public Map<String, Object> login(String username, String password) {
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

        // 更新登录信息
        user.setLoginIp(IpUtils.getIpAddr());
        user.setLoginDate(DateUtils.getNowDate());
        userMapper.updateById(user);

        recordLoginInfo(user.getUsername(), AuthConstants.LOGIN_SUCCESS, "登录成功");

        return userTokenService.createToken(user, user.getUserType());
    }

    /**
     * 退出登录
     */
    @Override
    public void logout(HttpServletRequest request) {
        String token = userTokenService.getUOToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JWTService.getUsername(JWTService.parseToken(token));
            userTokenService.delUserOnline(token);

            recordLoginInfo(username, AuthConstants.LOGOUT, "退出成功");
        }
    }

    /**
     * 刷新当前用户Token
     */
    @Override
    public void refreshToken(HttpServletRequest request) {
        String token = userTokenService.getUOToken(request);
        userTokenService.refreshToken(userTokenService.getUO(token));
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
        user.setPassword(PWCheckUtils.encryptPassword(password));
        user.setUserType(type);
        user.setCreateTime(DateUtils.getNowDate());

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
     * 密码校验
     */
    private void validatePassword(User user, String password) {
        String username = user.getUsername();

        Integer retryCounter = redisService.getCacheObject(PWCheckUtils.getWrongPWTimesKey(username));
        if (retryCounter == null) {
            retryCounter = 0;
        }

        if (retryCounter >= PWCheckUtils.PASSWORD_MAX_RETRY_COUNT) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟",
                    PWCheckUtils.PASSWORD_MAX_RETRY_COUNT, PWCheckUtils.PASSWORD_LOCK_TIME);
            throw new ServiceException(errMsg);
        }

        if (!PWCheckUtils.matchesPassword(password, user.getPassword())) {
            retryCounter++;
            redisService.setCacheObject(PWCheckUtils.getWrongPWTimesKey(username), retryCounter,
                    PWCheckUtils.PASSWORD_LOCK_TIME, java.util.concurrent.TimeUnit.MINUTES);
            throw new ServiceException("用户不存在/密码错误");
        } else {
            if (redisService.hasKey(PWCheckUtils.getWrongPWTimesKey(username))) {
                redisService.deleteObject(PWCheckUtils.getWrongPWTimesKey(username));
            }
        }
    }

    /**
     * 记录登录信息
     */
    private void recordLoginInfo(String username, String status, String message) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUsername(username);
        loginInfo.setIpaddr(IpUtils.getIpAddr());
        loginInfo.setMsg(message);

        if (StringUtils.equalsAny(status, AuthConstants.LOGIN_SUCCESS, AuthConstants.LOGOUT, AuthConstants.REGISTER)) {
            loginInfo.setStatus(AuthConstants.LOGIN_SUCCESS_STATUS);
        } else if (AuthConstants.LOGIN_FAIL.equals(status)) {
            loginInfo.setStatus(AuthConstants.LOGIN_FAIL_STATUS);
        }
        remoteLogService.saveLoginInfo(loginInfo, AuthConstants.INNER);
    }
}
