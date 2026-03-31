package com.sql.user.task;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sql.common.constants.TokenConstants;
import com.sql.common.redis.service.RedisService;

/**
 * 微信 access_token 定时刷新任务
 * 使用中控服务器统一获取和管理 access_token，在过期前 2 分钟主动刷新
 */
@Slf4j
@Component
public class WxAccessTokenTask {

    private static final String STABLE_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/stable_token";

    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 启动时立即获取一次，之后每分钟检查剩余 TTL，不足 2 分钟则刷新
     * 正常 expires_in 为 7200 秒，实际刷新间隔约 7198 秒触发一次
     */
    @Scheduled(fixedDelay = 60_000)
    public void refreshAccessToken() {
        Long ttl = redisService.getExpire(TokenConstants.WX_ACCESS_TOKEN);
        // ttl == -2 表示 key 不存在，ttl == -1 表示永不过期，其余为剩余秒数
        if (ttl != null && ttl > 120) {
            return;
        }
        fetchAndStore();
    }

    /**
     * 调用微信 stable_token 接口获取 access_token 并存入 Redis
     */
    private void fetchAndStore() {
        try {
            Map<String, Object> body = Map.of(
                    "grant_type", "client_credential",
                    "appid", appId,
                    "secret", appSecret,
                    "force_refresh", false);

            Map<String, Object> result = restTemplate.exchange(
                    STABLE_TOKEN_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(body),
                    new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();

            if (result == null || result.containsKey("errcode")) {
                log.error("获取微信 access_token 失败: {}", result);
                return;
            }

            String accessToken = (String) result.get("access_token");
            Number expiresIn = (Number) result.get("expires_in");

            if (accessToken == null || expiresIn == null) {
                log.error("微信 access_token 返回字段缺失: {}", result);
                return;
            }

            redisService.setCacheObject(TokenConstants.WX_ACCESS_TOKEN, accessToken,
                    expiresIn.longValue(), TimeUnit.SECONDS);
            log.info("微信 access_token 已刷新，有效期 {} 秒", expiresIn);

        } catch (Exception e) {
            log.error("刷新微信 access_token 异常", e);
        }
    }

    /**
     * 获取当前有效的 access_token，供其他服务调用
     */
    public String getAccessToken() {
        return redisService.getCacheObject(TokenConstants.WX_ACCESS_TOKEN);
    }
}
