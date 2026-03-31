package com.sql.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.sql.common.constants.AuthConstants;
import com.sql.common.constants.ContextHolderConstants;
import com.sql.common.entity.bo.AdminOnline;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.jwt.utils.TokenUtils;
import com.sql.common.header.ContextHolder;
import com.sql.common.tokens.AdminTokenService;
import com.sql.common.tokens.UserTokenService;
import com.sql.utils.ServletUtils;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {
    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 内部Feign调用不经过用户认证
        String source = ServletUtils.getHeader(request, AuthConstants.FROM_SOURCE);
        if (AuthConstants.INNER.equals(source)) {
            return true;
        }

        ContextHolder.setId(ServletUtils.getHeader(request, ContextHolderConstants.CH_ID));

        String type = ServletUtils.getHeader(request, ContextHolderConstants.CH_TYPE);
        ContextHolder.setType(type);

        String token = TokenUtils.getToken();
        if (Integer.parseInt(type) == 0) {
            AdminOnline ao = adminTokenService.getAO(token);
            adminTokenService.verifyToken(ao);

            ContextHolder.set(ContextHolderConstants.CH_ADMIN_ONLINE, ao);
            ContextHolder.setUsername(ServletUtils.getHeader(request, ContextHolderConstants.CH_USERNAME));
            ContextHolder.setToken(ServletUtils.getHeader(request, ContextHolderConstants.CH_TOKEN));
        } else {
            UserOnline uo = userTokenService.getUO(token);
            userTokenService.verifyToken(uo);

            ContextHolder.set(ContextHolderConstants.CH_USER_ONLINE, uo);
            ContextHolder.setSessionKey(ServletUtils.getHeader(request, ContextHolderConstants.CH_SESSION_KEY));
            ContextHolder.setOpenId(ServletUtils.getHeader(request, ContextHolderConstants.CH_OPENID));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        ContextHolder.remove();
    }
}
