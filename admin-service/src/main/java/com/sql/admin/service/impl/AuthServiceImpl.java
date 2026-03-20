package com.sql.admin.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.admin.dto.AdminInviteDTO;
import com.sql.admin.dto.AdminRegisterDTO;
import com.sql.admin.dto.AdminResetPasswordDTO;
import com.sql.admin.mapper.AdminMapper;
import com.sql.admin.service.AuthService;
import com.sql.api.RemoteLoginLogService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.constants.CacheConstants;
import com.sql.common.entity.AdminOnline;
import com.sql.common.entity.db.Admin;
import com.sql.common.entity.db.LoginInfo;
import com.sql.common.enums.AccountStatus;
import com.sql.common.exception.ServiceException;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.AdminTokenService;
import com.sql.utils.IpUtils;
import com.sql.utils.PasswordUtils;
import com.sql.utils.StringUtils;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.beans.factory.annotation.Value;

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
    private RemoteLoginLogService remoteLoginLogService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    /**
     * 邀请码过期时间 30min
     */
    private static final Long ADMIN_INVITE_EXPIRE = 30L;

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
        String inviteCode = registerDTO.getInviteCode();
        String inviteKey = AuthConstants.INVITE_CODE + inviteCode;

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
        String inviteKey = AuthConstants.INVITE_CODE + inviteCode;
        AdminInviteDTO inviteDTO = new AdminInviteDTO(referrer.getReferrerId(), storeId);

        redisService.setCacheObject(inviteKey, inviteDTO, ADMIN_INVITE_EXPIRE, TimeUnit.MINUTES);

        return inviteCode;
    }

    /**
     * 发送邮箱验证码
     */
    @Override
    public String sendEmailCode(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new ServiceException("邮箱不能为空");
        }

        // 校验邮箱是否绑定管理员账号
        Admin admin = adminMapper.selectByEmail(email);
        if (admin == null) {
            throw new ServiceException("该邮箱未绑定任何管理员账号");
        }

        // 生成6位随机验证码
        String emailCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

        // 存入Redis，有效期5分钟
        String codeKey = AuthConstants.EMAIL_CODE_KEY + email;
        redisService.setCacheObject(codeKey, emailCode, CacheConstants.EMAIL_CODE_EXPIRATION, TimeUnit.MINUTES);

        // 发送邮件
        MimeMessagePreparator msg = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("【LoveSport】忘记密码 - 邮箱验证码");
            message.setText(
                    "<div style='background-color:#0d1117; color:#ffffff; border:1px solid #30363d; padding: 20px; border-radius: 8px; font-family: Arial, sans-serif; font-size: 16px;'>"
                            + "<h2 style='color:#58a6ff;'>LoveSport 安全验证</h2>"
                            + "<p style='margin-top:10px;'>您好，</p>"
                            + "<p>这是您的 <strong>LoveSport</strong> 账号生成的临时验证码：</p>"
                            + "<p style='font-size:32px; font-weight:bold; color:#ffffff; background-color:#21262d; padding:10px 15px; display:inline-block; border-radius:6px; border:1px dashed #58a6ff;'>"
                            + emailCode + "</p>"
                            + "<p style='margin-top:20px;'>有效期5分钟</p>"
                            + "<p style='margin-top:20px;'>如非本人操作,请忽略此邮件</p>"
                            + "<hr style='margin-top:30px; border:none; border-top:1px solid #30363d;'>"
                            + "<p style='font-size:12px; color:#8b949e;'>loveSport 官方团队</p>"
                            + "</div>",
                    true);
        };
        mailSender.send(msg);

        log.info("向邮箱 {} 发送验证码成功", email);
        return emailCode;// FIXME:自动化测试使用，正式环境不应返回验证码
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
        String codeKey = AuthConstants.EMAIL_CODE_KEY + email;
        String cachedCode = redisService.getCacheObject(codeKey);
        if (cachedCode == null) {
            throw new ServiceException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(emailCode)) {
            throw new ServiceException("验证码错误");
        }
        // 验证码使用后立即删除
        redisService.deleteObject(codeKey);

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
            throw new ServiceException("用户不存在/密码错误");
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
