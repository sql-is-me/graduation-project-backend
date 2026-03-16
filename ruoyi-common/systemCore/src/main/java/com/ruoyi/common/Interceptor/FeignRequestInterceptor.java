package com.ruoyi.common.Interceptor;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import com.ruoyi.common.Constants.AuthConstants;
import com.ruoyi.common.Constants.ContextHolderConstants;
import com.ruoyi.utils.IpUtils;
import com.ruoyi.utils.ServletUtils;
import com.ruoyi.utils.StringUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 请求拦截器
 * 在内部服务间调用时自动传递用户信息和内部标识
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (StringUtils.isNotNull(httpServletRequest)) {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);

            // 传递用户信息请求头，防止丢失
            String id = headers.get(ContextHolderConstants.CH_ID);
            if (StringUtils.isNotEmpty(id)) {
                requestTemplate.header(ContextHolderConstants.CH_ID, id);
            }

            String token = headers.get(ContextHolderConstants.CH_TOKEN);
            if (StringUtils.isNotEmpty(token)) {
                requestTemplate.header(ContextHolderConstants.CH_TOKEN, token);
            }

            String username = headers.get(ContextHolderConstants.CH_USERNAME);
            if (StringUtils.isNotEmpty(username)) {
                requestTemplate.header(ContextHolderConstants.CH_USERNAME, username);
            }

            String authentication = headers.get(AuthConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication)) {
                requestTemplate.header(AuthConstants.AUTHORIZATION_HEADER, authentication);
            }

            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr());

            // 标记为内部请求，配合 @InnerAuth 注解使用
            requestTemplate.header(AuthConstants.FROM_SOURCE, AuthConstants.INNER);
        }
    }
}