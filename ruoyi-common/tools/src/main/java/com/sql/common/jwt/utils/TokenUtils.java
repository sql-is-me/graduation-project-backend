package com.sql.common.jwt.utils;

import com.sql.common.constants.AuthConstants;
import com.sql.common.constants.TokenConstants;
import com.sql.utils.ServletUtils;
import com.sql.utils.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 权限获取工具类
 * 
 * @author loveSport
 */
public class TokenUtils {

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
