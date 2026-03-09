package com.ruoyi.admin.service.Impl;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.admin.mapper.AdminMapper;
import com.ruoyi.admin.service.AuthService;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.enums.AccountStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.entity.Admin;
import com.ruoyi.common.tokens.service.AdminTokenService;
import com.ruoyi.common.utils.PWCheckUtils;

/**
 * 管理员登录与注册服务
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AdminMapper adminUserMapper;

    @Autowired
    private PWCheckUtils pwCheckUtils;

    @Autowired
    private AdminRecordLogService recordLogService;

    @Autowired
    private AdminTokenService adminTokenService;

    /**
     * 管理员登录
     */
    public Map<String, Object> login(String username, String password) {
        Admin admin;
        try {
            if (StringUtils.isAnyBlank(username, password)) {
                throw new ServiceException("用户/密码必须填写");
            }
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
            admin = adminUserMapper.selectByUsername(username);
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

        adminUserMapper.updateById(admin);
        return admin;
    }

    /**
     * 退出登录
     */
    public void logout(HttpServletRequest request) {
        String token = adminTokenService.getAOToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);
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

    // IP黑名单校验
    // String blackStr =
    // Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));if(IpUtils.isMatchedIp(blackStr,IpUtils.getIpAddr()))
    // {
    // recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL,
    // "很遗憾，访问IP已被列入系统黑名单");
    // throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
    // }

}
