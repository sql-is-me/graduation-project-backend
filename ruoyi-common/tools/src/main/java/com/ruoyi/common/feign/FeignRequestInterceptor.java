package com.ruoyi.common.feign;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import com.ruoyi.common.IpUtils;
import com.ruoyi.common.ServletUtils;
import com.ruoyi.common.StringUtils;
import com.ruoyi.common.Constants.AuthConstants;
import com.ruoyi.common.Constants.ContextHolderConstants;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 请求拦截器
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

            // FIXME:是否要传递在线管理员与用户对象？
            // FIXME：authentication并未发现有地方set过
            String authentication = headers.get(AuthConstants.AUTHORIZATION_HEADER);
            if (StringUtils.isNotEmpty(authentication)) {
                requestTemplate.header(AuthConstants.AUTHORIZATION_HEADER, authentication);
            }

            // 配置客户端IP
            requestTemplate.header("X-Forwarded-For", IpUtils.getIpAddr());
        }
    }
}