package com.sql.user.service.impl;

import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sql.common.constants.TokenConstants;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.exception.ServiceException;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.UserTokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * 微信 session_key 校验与重置服务实现
 */
@Slf4j
@Service
public class WxSessionServiceImpl {
    // TODO:待审查

    private static final String CHECK_SESSION_URL = "https://api.weixin.qq.com/wxa/checksession";
    private static final String RESET_SESSION_KEY_URL = "https://api.weixin.qq.com/wxa/resetusersessionkey";

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 确保 session_key 有效，若过期则重置
     * 业务层在使用 session_key 调用微信 API 前应先调用此方法
     *
     * @param uo 当前用户在线信息
     * @return 有效的 session_key
     * @throws ServiceException 若 session_key 已过期且重置失败
     */
    public String ensureSessionKey(UserOnline uo) {
        String openId = uo.getUserInfo().getOpenId();
        String oldSessionKey = uo.getSession_key();

        if (checkSessionKey(oldSessionKey, openId)) {
            return oldSessionKey;
        }

        // session_key 已过期，尝试重置
        String newSessionKey = resetSessionKey(oldSessionKey, openId);
        if (newSessionKey == null) {
            throw new ServiceException("登录态已失效，请重新登录");
        }

        // 删除旧缓存，更新为新 session_key
        String oldUoKey = TokenConstants.USER_SESSION_KEYS + oldSessionKey;
        redisService.deleteObject(oldUoKey);
        uo.setSession_key(newSessionKey);
        userTokenService.refreshCacheInfo(uo);

        return newSessionKey;
    }

    /**
     * 校验用户 session_key 是否仍然有效
     *
     * @param sessionKey 用户的 session_key
     * @param openId     用户的 openId
     * @return true 有效，false 已过期
     */
    public boolean checkSessionKey(String sessionKey, String openId) {
        try {
            String accessToken = redisService.getCacheObject(TokenConstants.WX_ACCESS_TOKEN);
            if (accessToken == null) {
                log.warn("微信 access_token 不可用，跳过 session_key 校验");
                return true;
            }

            String signature = hmacSha256(sessionKey, "");

            String url = UriComponentsBuilder.fromUriString(CHECK_SESSION_URL)
                    .queryParam("access_token", accessToken)
                    .toUriString();

            Map<String, Object> body = Map.of(
                    "openid", openId,
                    "signature", signature,
                    "sig_method", "hmac_sha256");

            Map<String, Object> result = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(body),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }).getBody();

            if (result == null) {
                log.error("checksession 返回为空");
                return true;
            }

            Number errcode = (Number) result.get("errcode");
            if (errcode != null && errcode.intValue() == 0) {
                return true;
            }

            log.info("session_key 已过期, openId={}, errcode={}, errmsg={}",
                    openId, errcode, result.get("errmsg"));
            return false;

        } catch (Exception e) {
            log.error("校验 session_key 异常", e);
            return true;
        }
    }

    /**
     * 重置用户 session_key
     *
     * @param sessionKey 旧的 session_key
     * @param openId     用户的 openId
     * @return 新的 session_key，失败返回 null
     */
    public String resetSessionKey(String sessionKey, String openId) {
        try {
            String accessToken = redisService.getCacheObject(TokenConstants.WX_ACCESS_TOKEN);
            if (accessToken == null) {
                log.error("微信 access_token 不可用，无法重置 session_key");
                return null;
            }

            String signature = hmacSha256(sessionKey, "");

            String url = UriComponentsBuilder.fromUriString(RESET_SESSION_KEY_URL)
                    .queryParam("access_token", accessToken)
                    .toUriString();

            Map<String, Object> body = Map.of(
                    "openid", openId,
                    "signature", signature,
                    "sig_method", "hmac_sha256");

            Map<String, Object> result = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(body),
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }).getBody();

            if (result == null) {
                log.error("resetusersessionkey 返回为空");
                return null;
            }

            Number errcode = (Number) result.get("errcode");
            if (errcode != null && errcode.intValue() == 0) {
                String newSessionKey = (String) result.get("session_key");
                log.info("session_key 重置成功, openId={}", openId);
                return newSessionKey;
            }

            log.error("重置 session_key 失败, openId={}, errcode={}, errmsg={}",
                    openId, errcode, result.get("errmsg"));
            return null;

        } catch (Exception e) {
            log.error("重置 session_key 异常", e);
            return null;
        }
    }

    /**
     * HMAC-SHA256 签名
     */
    private String hmacSha256(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 签名失败", e);
        }
    }
}
