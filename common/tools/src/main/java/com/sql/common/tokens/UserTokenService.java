package com.sql.common.tokens;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sql.common.constants.CacheConstants;
import com.sql.common.constants.JWTConstants;
import com.sql.common.constants.TokenConstants;
import com.sql.common.jwt.service.JWTService;
import com.sql.common.jwt.utils.TokenUtils;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.User;
import com.sql.common.redis.service.RedisService;
import com.sql.utils.IpUtils;

/**
 * 用户/教练 token验证处理
 */
@Slf4j
@Component
public class UserTokenService {
    private final static Long SESSION_KEY_REFRESH_THRESHOLD_MINUTES = CacheConstants.REFRESH_TIME
            * CacheConstants.MILLIS_MINUTE;

    @Autowired
    private RedisService redisService;

    /**
     * 创建令牌
     * 用户/教练版本
     */
    public String createToken(User user, String session_key) {
        UserOnline uo = new UserOnline();
        uo.setSession_key(session_key);
        uo.setUserInfo(user);
        uo.setIpaddr(IpUtils.getIpAddr());
        uo.setLoginTime(LocalDateTime.now().withNano(0));
        uo.setExpireTime(uo.getLoginTime().plusMinutes(CacheConstants.TOKEN_EXPIRE_TIME));

        checkAndDeleteCacheObject(user.getUserId());
        createAndSetCacheObject(uo);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put(JWTConstants.DETAILS_SESSION_KEY, session_key);
        claimsMap.put(JWTConstants.DETAILS_ID, user.getUserId());
        claimsMap.put(JWTConstants.DETAILS_USERNAME, user.getOpenId());
        claimsMap.put(JWTConstants.DETAILS_TYPE, "1"); // 1表示用户端

        // 接口返回信息
        String accessToken = JWTService.createToken(claimsMap);

        return accessToken;
    }

    /**
     * 创建并设置缓存对象
     */
    public void createAndSetCacheObject(UserOnline uo) {
        String uoKey = TokenConstants.USER_SESSION_KEYS + uo.getSession_key();
        redisService.setCacheObject(uoKey, uo, CacheConstants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

        // 维护 userId -> session_key 反向映射，用于强制下线
        String mappingKey = TokenConstants.USER_SESSION_KEY_MAPPING + uo.getUserInfo().getUserId();
        redisService.setCacheObject(mappingKey, uo.getSession_key(), CacheConstants.TOKEN_EXPIRE_TIME,
                TimeUnit.MINUTES);
    }

    /**
     * 若已经登录，则删除UO缓存对象，强制下线
     * 
     * 用于单点登录
     */
    public void checkAndDeleteCacheObject(Long userId) {
        String mappingKey = TokenConstants.USER_SESSION_KEY_MAPPING + userId;
        String session_key = redisService.getCacheObject(mappingKey);
        if (session_key != null) {
            String uoKey = TokenConstants.USER_SESSION_KEYS + session_key;
            redisService.deleteObject(uoKey);
            redisService.deleteObject(mappingKey);
        }
    }

    /**
     * 验证令牌有效期，相差不足阈值时间，自动刷新缓存
     */
    public void verifyToken(UserOnline uo) {
        LocalDateTime expireTime = uo.getExpireTime();
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        if (expireTime.isAfter(currentTime)) {
            long minutesDiff = java.time.Duration.between(currentTime, expireTime).toMinutes();
            if (minutesDiff <= SESSION_KEY_REFRESH_THRESHOLD_MINUTES) {
                resetExpireTime(uo);
            }
        }
    }

    /**
     * 刷新用户缓存过期时间
     */
    public void resetExpireTime(UserOnline uo) {
        uo.setExpireTime(LocalDateTime.now().withNano(0).plusMinutes(CacheConstants.TOKEN_EXPIRE_TIME));
        refreshCacheInfo(uo);
    }

    /**
     * 重设缓存中个人信息
     */
    public void refreshCacheInfo(UserOnline uo) {
        createAndSetCacheObject(uo);
    }

    /**
     * 获取当前登录用户的access_token
     * 
     * @return access_token
     */
    public String getUOToken(HttpServletRequest request) {
        return TokenUtils.getToken(request);
    }

    /**
     * 获取当前登录用户存储在redis中的key
     * 
     * @param access_token
     * @return uoKey
     */
    public String getUOKey(String token) {
        return TokenConstants.USER_SESSION_KEYS + JWTService.getSessionKey(JWTService.parseToken(token));
    }

    /**
     * 获取当前登录用户存储在redis中的key
     *
     * @param uo
     * @return uoKey
     */
    public String getUOKey(UserOnline uo) {
        return TokenConstants.USER_SESSION_KEYS + uo.getSession_key();
    }

    /**
     * 获取用户身份信息
     *
     * @param token JWT令牌
     * @return 用户在线信息
     */
    public UserOnline getUO(String token) {
        try {
            String uoKey = getUOKey(token);
            return redisService.getCacheObject(uoKey);
        } catch (Exception e) {
            log.error("获取用户信息异常'{}'", e.getMessage());
        }
        return null;
    }

    /**
     * 删除用户缓存信息
     */
    public void delUserOnline(UserOnline uo) {
        Long userId = uo.getUserInfo().getUserId();

        // 删除uo缓存
        String uoKey = getUOKey(uo);
        redisService.deleteObject(uoKey);

        // 删除 userId -> session_key 反向映射缓存
        String mappingKey = TokenConstants.USER_SESSION_KEY_MAPPING + userId;
        redisService.deleteObject(mappingKey);
    }
}
