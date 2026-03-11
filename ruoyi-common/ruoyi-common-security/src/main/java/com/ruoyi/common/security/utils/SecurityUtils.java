package com.ruoyi.common.security.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ruoyi.common.Constants.AuthConstants;
import com.ruoyi.common.Constants.TokenConstants;
import com.ruoyi.common.contextHolder.ContextHolder;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.model.LoginUser;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 权限获取工具类
 * 
 * @author ruoyi
 */
public class SecurityUtils {

    /**
     * 获取请求token
     */
    public static String getToken() {
        return getToken(ServletUtils.getRequest());
    }

    /**
     * 根据request获取请求token
     */
    public static String getToken(HttpServletRequest request) {
        // 从header获取token标识
        String token = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        return replaceTokenPrefix(token);
    }

    /**
     * 裁剪token前缀
     */
    public static String replaceTokenPrefix(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, "");
        }
        return token;
    }
}
