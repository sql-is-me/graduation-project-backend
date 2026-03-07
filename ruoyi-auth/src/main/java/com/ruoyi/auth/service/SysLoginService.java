package com.ruoyi.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.ruoyi.auth.dto.AdminInviteInfo;
import com.ruoyi.auth.dto.AdminRegisterDTO;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.enums.UserStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysLoginService {
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysRecordLogService recordLogService;

    @Autowired
    private RedisService redisService;

    private static final String ADMIN_TYPE_STORE = "STORE";

    /**
     * 登录
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
        // 查询用户信息
        R<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);

        if (R.FAIL == userResult.getCode()) {
            throw new ServiceException(userResult.getMsg());
        }

        LoginUser userInfo = userResult.getData();
        SysUser user = userResult.getData().getSysUser();
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "对不起，您的账号已被删除");
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        passwordService.validate(user, password);
        recordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        recordLoginInfo(user.getUserId());
        return userInfo;
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        // 更新用户登录IP
        sysUser.setLoginIp(IpUtils.getIpAddr());
        // 更新用户登录时间
        sysUser.setLoginDate(DateUtils.getNowDate());
        remoteUserService.recordUserLogin(sysUser, SecurityConstants.INNER);
    }

    public void logout(String loginName) {
        recordLogService.recordLogininfor(loginName, Constants.LOGOUT, "退出成功");
    }

    /**
     * 注册
     */
    public void register(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 注册用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPwdUpdateDate(DateUtils.getNowDate());
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        R<?> registerResult = remoteUserService.registerUserInfo(sysUser, SecurityConstants.INNER);

        if (R.FAIL == registerResult.getCode()) {
            throw new ServiceException(registerResult.getMsg());
        }
        recordLogService.recordLogininfor(username, Constants.REGISTER, "注册成功");
    }

    /**
     * 管理员注册
     */
    public void registerAdmin(AdminRegisterDTO dto) {
        if (dto == null) {
            throw new ServiceException("请求参数不能为空");
        }
        String username = dto.getUsername();
        String password = dto.getPassword();
        if (StringUtils.isAnyBlank(username, password, dto.getInviteCode())) {
            throw new ServiceException("用户名/密码/推荐码必须填写");
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

        String inviteKey = dto.getInviteCode();
        AdminInviteInfo inviteInfo = redisService.getCacheObject(inviteKey);
        if (inviteInfo == null) {
            throw new ServiceException("推荐码无效或已过期");
        }
        if (!dto.getReferrerId().equals(inviteInfo.getReferrerId())) {
            throw new ServiceException("推荐人不匹配");
        }

        R<LoginUser> referrerResult = remoteUserService.getUserInfoById(dto.getReferrerId(),
                SecurityConstants.INNER);
        if (R.FAIL == referrerResult.getCode() || referrerResult.getData() == null) {
            throw new ServiceException("推荐人不存在");
        }
        SysUser referrer = referrerResult.getData().getSysUser();

        Long storeId;
        if (referrer.isAdmin()) {
            if (dto.getStoreId() == null || dto.getStoreId() <= 0) {
                throw new ServiceException("顶级管理员推荐注册需指定门店ID");
            }
            storeId = dto.getStoreId();
        } else {
            if (!ADMIN_TYPE_STORE.equals(referrer.getAdminType())) {
                throw new ServiceException("推荐人无权限生成门店管理员");
            }
            storeId = referrer.getStoreId();
            if (storeId == null || storeId <= 0) {
                throw new ServiceException("推荐人门店信息缺失");
            }
        }
        if (inviteInfo.getStoreId() != null && !inviteInfo.getStoreId().equals(storeId)) {
            throw new ServiceException("推荐码门店不匹配");
        }

        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPwdUpdateDate(DateUtils.getNowDate());
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        sysUser.setAdminType(ADMIN_TYPE_STORE);
        sysUser.setReferrerId(dto.getReferrerId());
        sysUser.setStoreId(storeId);

        R<?> registerResult = remoteUserService.registerAdminUserInfo(sysUser, SecurityConstants.INNER);
        if (R.FAIL == registerResult.getCode()) {
            throw new ServiceException(registerResult.getMsg());
        }
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
            if (storeId == null || storeId <= 0) {
                throw new ServiceException("顶级管理员生成邀请码需指定门店ID");
            }
        } else {
            if (!ADMIN_TYPE_STORE.equals(referrer.getAdminType())) {
                throw new ServiceException("仅顶级管理员或门店管理员可生成邀请码");
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
