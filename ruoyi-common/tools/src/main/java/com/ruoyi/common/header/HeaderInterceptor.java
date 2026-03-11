package com.ruoyi.common.header;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.ruoyi.common.Constants.ContextHolderConstants;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.entity.UserOnline;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.common.tokens.AdminTokenService;
import com.ruoyi.common.tokens.UserTokenService;

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

        ContextHolder.setId(ServletUtils.getHeader(request, ContextHolderConstants.CH_ID));
        ContextHolder.setUsername(ServletUtils.getHeader(request, ContextHolderConstants.CH_USERNAME));
        ContextHolder.setToken(ServletUtils.getHeader(request, ContextHolderConstants.CH_TOKEN));

        String type = ServletUtils.getHeader(request, ContextHolderConstants.CH_TYPE);
        ContextHolder.setType(type);

        String token = SecurityUtils.getToken();
        if (Integer.valueOf(type) == 0) {
            AdminOnline ao = adminTokenService.getAO(token);
            adminTokenService.verifyToken(ao);

            ContextHolder.set(ContextHolderConstants.CH_ADMIN_ONLINE, ao);
        } else {
            UserOnline uo = userTokenService.getUO(token);
            userTokenService.verifyToken(uo);

            ContextHolder.set(ContextHolderConstants.CH_USER_ONLINE, uo);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        ContextHolder.remove();
    }
}
