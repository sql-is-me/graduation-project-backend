package com.ruoyi.admin.service.Impl;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.admin.dto.AdminInviteInfo;
import com.ruoyi.admin.dto.AdminRegisterDTO;
import com.ruoyi.admin.mapper.AdminMapper;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.enums.AccountStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 管理员登录与注册服务
 */
@Service
public class LoginService {

    @Autowired
    private AdminMapper adminUserMapper;

    @Autowired
    private AdminPasswordService passwordService;

    @Autowired
    private AdminRecordLogService recordLogService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AdminPermissionService permissionService;

    private static final String ADMIN_TYPE_STORE = "STORE";

    /**
     * 管理员登录
     */
    public LoginUser login(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码不在指定范围");
            throw new ServiceException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名不在指定范围");
            throw new ServiceException("用户名不在指定范围");
        }
        // IP黑名单校验
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "很遗憾，访问IP已被列入系统黑名单");
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息（直连数据库）
        SysUser user = adminUserMapper.selectUserByUserName(username);
        if (user == null) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名或密码错误");
            throw new ServiceException("用户名或密码错误");
        }
        if (AccountStatus.DELETED.getCode().equals(user.getDelFlag())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "对不起，您的账号已被删除");
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        if (AccountStatus.DISABLE.getCode().equals(user.getStatus())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        passwordService.validate(user, password);
        recordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        recordLoginInfo(user.getUserId());

        // 构建LoginUser（包含权限信息）
        LoginUser loginUser = new LoginUser();
        loginUser.setSysUser(user);
        // 获取角色和权限
        Set<String> roles = permissionService.getRolePermission(user);
        Set<String> permissions = permissionService.getMenuPermission(user);
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);
        return loginUser;
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        adminUserMapper.updateLoginInfo(sysUser);
    }

    /**
     * 退出登录
     */
    public void logout(String loginName) {
        recordLogService.recordLogininfor(loginName, Constants.LOGOUT, "退出成功");
    }

    /**
     * 管理员注册（需邀请码）
     */
    public void registerAdmin(AdminRegisterDTO dto) {
        if (dto == null) {
            throw new ServiceException("请求参数不能为空");
        }
        String username = dto.getUsername();
        String password = dto.getPassword();
        if (StringUtils.isAnyBlank(username, password, dto.getInviteCode())) {
            throw new ServiceException("用户名/密码/邀请码必须填写");
        }
        if (dto.getReferrerId() == null || dto.getReferrerId() <= 0) {
            throw new ServiceException("推荐人ID不合法");
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
        String inviteKey = dto.getInviteCode();
        AdminInviteInfo inviteInfo = redisService.getCacheObject(inviteKey);
        if (inviteInfo == null) {
            throw new ServiceException("邀请码无效或已过期");
        }
        if (!dto.getReferrerId().equals(inviteInfo.getReferrerId())) {
            throw new ServiceException("推荐人不匹配");
        }

        // 校验推荐人信息
        SysUser referrer = adminUserMapper.selectUserById(dto.getReferrerId());
        if (referrer == null) {
            throw new ServiceException("推荐人不存在");
        }

        Long storeId;
        if (referrer.isAdmin()) {
            // 顶级管理员推荐：需指定门店ID
            if (dto.getStoreId() == null || dto.getStoreId() <= 0) {
                throw new ServiceException("顶级管理员推荐注册需指定门店ID");
            }
            storeId = dto.getStoreId();
        } else {
            // 地区管理员推荐：继承门店ID
            if (!ADMIN_TYPE_STORE.equals(referrer.getAdminType())) {
                throw new ServiceException("推荐人无权限生成地区管理员");
            }
            storeId = referrer.getStoreId();
            if (storeId == null || storeId <= 0) {
                throw new ServiceException("推荐人门店信息缺失");
            }
        }
        if (inviteInfo.getStoreId() != null && !inviteInfo.getStoreId().equals(storeId)) {
            throw new ServiceException("邀请码门店不匹配");
        }

        // 校验用户名唯一性
        SysUser existUser = adminUserMapper.checkUserNameUnique(username);
        if (existUser != null) {
            throw new ServiceException("注册账号'" + username + "'已存在");
        }

        // 创建用户
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPwdUpdateDate(DateUtils.getNowDate());
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        sysUser.setAdminType(ADMIN_TYPE_STORE);
        sysUser.setReferrerId(dto.getReferrerId());
        sysUser.setStoreId(storeId);

        int rows = adminUserMapper.insertUser(sysUser);
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
    public String generateAdminInviteCode(LoginUser loginUser, Long storeId) {
        if (loginUser == null || loginUser.getSysUser() == null) {
            throw new ServiceException("未登录或登录态失效");
        }
        SysUser referrer = loginUser.getSysUser();
        Long referrerId = referrer.getUserId();
        if (referrerId == null) {
            throw new ServiceException("推荐人信息缺失");
        }

        if (referrer.isAdmin()) {
            // 顶级管理员需指定门店ID
            if (storeId == null || storeId <= 0) {
                throw new ServiceException("顶级管理员生成邀请码需指定门店ID");
            }
        } else {
            if (!ADMIN_TYPE_STORE.equals(referrer.getAdminType())) {
                throw new ServiceException("仅顶级管理员或地区管理员可生成邀请码");
            }
            storeId = referrer.getStoreId();
            if (storeId == null || storeId <= 0) {
                throw new ServiceException("推荐人门店信息缺失");
            }
        }

        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        AdminInviteInfo inviteInfo = new AdminInviteInfo();
        inviteInfo.setReferrerId(referrerId);
        inviteInfo.setStoreId(storeId);

        redisService.setCacheObject(code, inviteInfo,
                CacheConstants.ADMIN_INVITE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return code;
    }
}
