package com.sql.admin.service.impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.admin.dto.AdminInviteDTO;
import com.sql.admin.dto.AdminRegisterDTO;
import com.sql.admin.mapper.AdminMapper;
import com.sql.admin.service.AuthService;
import com.sql.api.RemoteLoginLogService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.jwt.service.JWTService;
import com.sql.common.entity.Admin;
import com.sql.common.entity.AdminOnline;
import com.sql.common.entity.LoginInfo;
import com.sql.common.enums.AccountStatus;
import com.sql.common.exception.ServiceException;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.AdminTokenService;
import com.sql.utils.DateUtils;
import com.sql.utils.IpUtils;
import com.sql.utils.PWCheckUtils;
import com.sql.utils.StringUtils;

/**
 * 管理员登录与注册服务
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteLoginLogService remoteLoginLogService;

    /**
     * 邀请码过期时间 30min
     */
    private static final Long ADMIN_INVITE_EXPIRE = 30L;

    /**
     * 管理员登录
     */
    @Override
    public Map<String, Object> login(String username, String password) {
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

        admin.setLoginIp(IpUtils.getIpAddr());
        admin.setLoginDate(DateUtils.getNowDate());
        adminMapper.updateById(admin);

        recordLoginInfo(admin.getUsername(), AuthConstants.LOGIN_SUCCESS, "登录成功");

        return adminTokenService.createToken(admin);
    }

    /**
     * 退出登录
     */
    @Override
    public void logout(HttpServletRequest request) {
        String token = adminTokenService.getAOToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JWTService.getUsername(JWTService.parseToken(token));
            // 删除用户缓存记录
            adminTokenService.delAdminOnline(token);

            recordLoginInfo(username, AuthConstants.LOGOUT, "退出成功");
        }
    }

    /**
     * 刷新当前管理员Token
     */
    @Override
    public void refreshToken(HttpServletRequest request) {
        String token = adminTokenService.getAOToken(request);
        adminTokenService.refreshToken(adminTokenService.getAO(token));
    }

    /**
     * 管理员注册（需邀请码）
     */
    @Override
    public void register(AdminRegisterDTO registerDTO) {
        if (registerDTO == null) {
            throw new ServiceException("请求参数不能为空");
        }

        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        if (StringUtils.isAnyBlank(username, password, registerDTO.getInviteCode())) {
            throw new ServiceException("用户名/密码/邀请码必须填写");
        }
        if (username.length() < AuthConstants.USERNAME_MIN_LENGTH
                || username.length() > AuthConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < AuthConstants.PASSWORD_MIN_LENGTH
                || password.length() > AuthConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 校验邀请码
        String inviteKey = registerDTO.getInviteCode();
        AdminInviteDTO inviteDTO = redisService.getCacheObject(inviteKey);
        if (inviteDTO == null) {
            throw new ServiceException("邀请码无效或已过期");
        }

        // 校验用户名唯一性
        Admin existAdmin = adminMapper.selectByUsername(username);
        if (existAdmin != null) {
            throw new ServiceException("账号'" + username + "'已存在");
        }

        // 创建用户
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setNickName(username);
        admin.setPassword(PWCheckUtils.encryptPassword(password));
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
        if (referrer.isTopAdmin()) {
            // 顶级管理员需指定门店ID
            if (storeId == null) {
                throw new ServiceException("顶级管理员生成邀请码需指定门店ID");
            }
        } else {
            storeId = referrer.getStoreId();
            if (storeId == null) {
                throw new ServiceException("推荐人门店信息缺失");
            }
        }

        String inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        AdminInviteDTO inviteDTO = new AdminInviteDTO(referrer.getReferrerId(), storeId);

        redisService.setCacheObject(inviteCode, inviteDTO, ADMIN_INVITE_EXPIRE, TimeUnit.MINUTES);

        return inviteCode;
    }

    /**
     * 密码校验
     */
    public void validatePassword(Admin admin, String password) {
        String username = admin.getUsername();

        Integer retryCounter = redisService.getCacheObject(PWCheckUtils.getWrongPWTimesKey(username));
        if (retryCounter == null) {
            retryCounter = 0;
        }

        if (retryCounter >= PWCheckUtils.PASSWORD_MAX_RETRY_COUNT) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", PWCheckUtils.PASSWORD_MAX_RETRY_COUNT,
                    PWCheckUtils.PASSWORD_LOCK_TIME);
            throw new ServiceException(errMsg);
        }

        if (!PWCheckUtils.matchesPassword(password, admin.getPassword())) {
            retryCounter++;

            redisService.setCacheObject(PWCheckUtils.getWrongPWTimesKey(username), retryCounter,
                    PWCheckUtils.PASSWORD_LOCK_TIME, TimeUnit.MINUTES);
            throw new ServiceException("用户不存在/密码错误");
        } else {
            if (redisService.hasKey(PWCheckUtils.getWrongPWTimesKey(username))) {
                redisService.deleteObject(PWCheckUtils.getWrongPWTimesKey(username));
            }
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
        loginInfo.setUsername(username);
        loginInfo.setIpaddr(IpUtils.getIpAddr());
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
