package com.ruoyi.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ruoyi.common.ServletUtils;
import com.ruoyi.common.StringUtils;
import com.ruoyi.common.Constants.AuthConstants;
import com.ruoyi.common.Constants.ContextHolderConstants;
import com.ruoyi.common.Constants.HttpStatusConstants;
import com.ruoyi.common.Constants.TokenConstants;
import com.ruoyi.common.JWT.JWTService;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.gateway.config.properties.IgnoreWhiteProperties;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

/**
 * 网关鉴权
 * 
 * @author ruoyi
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    // 排除过滤的 uri 地址，nacos自行添加
    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Autowired
    private RedisService redisService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest.Builder mutate = request.mutate();

        String url = request.getURI().getPath();
        // 跳过白名单上的url
        if (StringUtils.matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }

        String token = getToken(request);
        if (StringUtils.isEmpty(token)) { // TODO:配置中心配置登录和注册白名单
            return unauthorizedResponse(exchange, "令牌不能为空");
        }

        Claims claims = JWTService.parseToken(token);
        if (claims == null) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }

        String id = JWTService.getId(claims);
        String username = JWTService.getUsername(claims);
        String type = JWTService.getType(claims);
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(username) || StringUtils.isEmpty(type)) {
            return unauthorizedResponse(exchange, "令牌验证失败");
        }

        String UUIDtoken = JWTService.getKey(claims);
        String UUIDKey;
        if (type.equals("0")) {
            UUIDKey = TokenConstants.ADMIN_TOKENS + UUIDtoken;
        } else if (type.equals("1")) {
            UUIDKey = TokenConstants.USER_TOKENS + UUIDtoken;
        } else {
            return unauthorizedResponse(exchange, "令牌用户类型异常");
        }
        boolean isOnline = redisService.hasKey(UUIDKey);
        if (!isOnline) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }

        // 设置用户信息到请求
        addHeader(mutate, ContextHolderConstants.CH_TOKEN, UUIDtoken);
        addHeader(mutate, ContextHolderConstants.CH_ID, id);
        addHeader(mutate, ContextHolderConstants.CH_USERNAME, username);
        addHeader(mutate, ContextHolderConstants.CH_TYPE, type);

        // 内部请求来源参数清除
        removeHeader(mutate, SecurityConstants.FROM_SOURCE);

        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = ServletUtils.urlEncode(valueStr);
        mutate.header(name, valueEncode);
    }

    private void removeHeader(ServerHttpRequest.Builder mutate, String name) {
        mutate.headers(httpHeaders -> httpHeaders.remove(name)).build();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{},错误信息:{}", exchange.getRequest().getPath(), msg);
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatusConstants.UNAUTHORIZED);
    }

    /**
     * 获取请求token
     * 
     * @param request 请求
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(AuthConstants.AUTHORIZATION_HEADER);

        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, StringUtils.EMPTY);
        }
        return token;
    }

    @Override
    public int getOrder() {
        return -200;
    }
}