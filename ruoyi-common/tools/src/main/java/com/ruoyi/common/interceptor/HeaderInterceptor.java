package com.ruoyi.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.ruoyi.common.Constants.JWTConstants;
import com.ruoyi.common.core.context.SecurityContextHolder;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.common.tokens.AdminTokenService;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author ruoyi
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {
    @Autowired
    private AdminTokenService adminTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        SecurityContextHolder.setId(ServletUtils.getHeader(request, JWTConstants.DETAILS_ID));
        SecurityContextHolder.setUsername(ServletUtils.getHeader(request, JWTConstants.DETAILS_USERNAME));
        SecurityContextHolder.setToken(ServletUtils.getHeader(request, JWTConstants.DETAILS_TOKEN));

        String type = ServletUtils.getHeader(request, JWTConstants.DETAILS_TYPE);
        SecurityContextHolder.setType(type);

        // TODO:按照type分为adminOnline和userOnline
        String token = SecurityUtils.getToken();
        // if (StringUtils.isEmpty(token)) {
        // LoginUser loginUser = AuthUtil.getLoginUser(token);
        // if (StringUtils.isNotNull(loginUser)) {
        // AuthUtil.verifyLoginUserExpire(loginUser);
        // SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
        // }
        // }
        if (Integer.valueOf(type) == 0) {
            AdminOnline ao = adminTokenService.getAO(token);
            verifyLoginUserExpire
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
