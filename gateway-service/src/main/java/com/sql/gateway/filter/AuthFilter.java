package com.sql.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.sql.common.constants.AuthConstants;
import com.sql.common.constants.ContextHolderConstants;
import com.sql.common.constants.HttpStatusConstants;
import com.sql.common.constants.TokenConstants;
import com.sql.common.jwt.service.JWTService;
import com.sql.common.redis.service.RedisService;
import com.sql.gateway.config.properties.IgnoreWhiteProperties;
import com.sql.utils.ServletUtils;
import com.sql.utils.StringUtils;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

/**
 * 网关鉴权
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
        if (StringUtils.isEmpty(token)) {
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

        boolean isOnline = false;
        String uuidToken = null;
        String session_key = null;
        if (type.equals("0")) {
            uuidToken = JWTService.getToken(claims);
            String aoKey = TokenConstants.ADMIN_TOKENS + uuidToken;
            isOnline = redisService.hasKey(aoKey);
        } else if (type.equals("1")) {
            session_key = JWTService.getSessionKey(claims);
            String uoKey = TokenConstants.USER_SESSION_KEYS + session_key;
            isOnline = redisService.hasKey(uoKey);
        } else {
            return unauthorizedResponse(exchange, "令牌用户类型异常");
        }

        if (!isOnline) {
            return unauthorizedResponse(exchange, "登录状态已过期");
        }

        // 设置用户信息到请求
        addHeader(mutate, ContextHolderConstants.CH_TYPE, type);
        addHeader(mutate, ContextHolderConstants.CH_ID, id);

        if (type.equals("0")) {
            addHeader(mutate, ContextHolderConstants.CH_TOKEN, uuidToken);
            addHeader(mutate, ContextHolderConstants.CH_USERNAME, username);
        } else {
            addHeader(mutate, ContextHolderConstants.CH_SESSION_KEY, session_key);
            addHeader(mutate, ContextHolderConstants.CH_OPENID, username);
        }

        // 内部请求来源参数清除
        removeHeader(mutate, AuthConstants.FROM_SOURCE);

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