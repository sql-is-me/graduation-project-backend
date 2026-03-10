package com.ruoyi.admin.service.Impl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.admin.dto.AdminInviteDTO;
import com.ruoyi.admin.dto.AdminRegisterDTO;
import com.ruoyi.admin.mapper.AdminMapper;
import com.ruoyi.admin.service.AuthService;
import com.ruoyi.common.JWT.JWTService;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.enums.AccountStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.entity.Admin;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.common.tokens.AdminTokenService;
import com.ruoyi.common.verifier.PWCheckUtils;

/**
 * 管理员登录与注册服务
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PWCheckUtils pwCheckUtils;

    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AdminRecordLogService recordLogService;

    /**
     * 邀请码过期时间 30min
     */
    private static final Long ADMIN_INVITE_EXPIRE = 30L;

    /**
     * 管理员登录
     */
    public Map<String, Object> login(String username, String password) {
        Admin admin;
        try {
            // 密码如果不在指定范围内 错误
            if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                    || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
                throw new ServiceException("密码长度不符合要求");
            }
            // 用户名不在指定范围内 错误
            if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                    || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
                throw new ServiceException("用户名长度不符合要求");
            }

            // 查询用户信息（直连数据库）
            admin = adminMapper.selectByUsername(username);
            if (admin == null) {
                throw new ServiceException("用户名或密码错误");
            }
            if (AccountStatus.DISABLE.getCode().equals(admin.getStatus())) {
                throw new ServiceException("账号已停用，请联系管理员");
            }

            pwCheckUtils.validate(admin, password);
        } catch (ServiceException e) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage());
            throw new ServiceException(e.getMessage());
        }

        admin = updateLoginInfo(admin);

        return adminTokenService.createToken(admin);
    }

    /**
     * 更新登录信息
     */
    public Admin updateLoginInfo(Admin admin) {
        recordLogService.recordLogininfor(admin.getUsername(), Constants.LOGIN_SUCCESS, "登录成功");

        admin.setLoginIp(IpUtils.getIpAddr());
        admin.setLoginDate(DateUtils.getNowDate());

        adminMapper.updateById(admin);
        return admin;
    }

    /**
     * 退出登录
     */
    public void logout(HttpServletRequest request) {
        String token = adminTokenService.getAOToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JWTService.getUsername(JWTService.parseToken(token));
            // 删除用户缓存记录
            adminTokenService.delAdminOnline(token);

            recordLogService.recordLogininfor(username, Constants.LOGOUT, "退出成功");
        }
    }

    /**
     * 刷新当前管理员Token
     */
    public void refreshToken(HttpServletRequest request) {
        String token = adminTokenService.getAOToken(request);
        adminTokenService.refreshToken(adminTokenService.getAO(token));
    }

    /**
     * 管理员注册（需邀请码）
     */
    public void register(AdminRegisterDTO registerDTO) {
        if (registerDTO == null) {
            throw new ServiceException("请求参数不能为空");
        }

        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        if (StringUtils.isAnyBlank(username, password, registerDTO.getInviteCode())) {
            throw new ServiceException("用户名/密码/邀请码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
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
        admin.setPassword(SecurityUtils.encryptPassword(password));
        admin.setStoreId(inviteDTO.getStoreId());
        admin.setReferrerId(inviteDTO.getReferrerId());

        int rows = adminMapper.insert(admin);
        if (rows <= 0) {
            throw new ServiceException("注册失败，请联系管理员");
        }

        // 注册成功后删除邀请码（一次性使用）
        redisService.deleteObject(inviteKey);
        recordLogService.recordLogininfor(username, Constants.REGISTER, "管理员注册成功");
    }

    /**
     * 生成管理员邀请码
     */
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

}
