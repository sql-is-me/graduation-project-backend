package com.sql.common.tokens;

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
import com.sql.common.entity.User;
import com.sql.common.entity.UserOnline;
import com.sql.common.redis.service.RedisService;
import com.sql.utils.IpUtils;
import com.sql.utils.StringUtils;
import com.sql.utils.uuid.IdUtils;

/**
 * 用户/教练 token验证处理
 */
@Slf4j
@Component
public class UserTokenService {
    private final static Long TOKEN_REFRESH_THRESHOLD_MINUTES = CacheConstants.REFRESH_TIME
            * CacheConstants.MILLIS_MINUTE;

    @Autowired
    private RedisService redisService;

    /**
     * 创建令牌
     *
     * @param user     用户信息
     * @param userType 用户类型（0:会员 1:教练）
     */
    public Map<String, Object> createToken(User user, String userType) {
        String token = IdUtils.fastUUID();

        UserOnline uo = new UserOnline();
        uo.setUserInfo(user);
        uo.setIpaddr(IpUtils.getIpAddr());
        uo.setToken(token);
        uo.setUserType(userType);
        refreshToken(uo);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put(JWTConstants.DETAILS_TOKEN, token);
        claimsMap.put(JWTConstants.DETAILS_ID, user.getUserId());
        claimsMap.put(JWTConstants.DETAILS_USERNAME, user.getUsername());
        claimsMap.put(JWTConstants.DETAILS_TYPE, "1"); // 1表示用户端

        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<String, Object>();
        rspMap.put("access_token", JWTService.createToken(claimsMap));
        rspMap.put("expires_in", CacheConstants.TOKEN_EXPIRE_TIME);
        return rspMap;
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     */
    public void verifyToken(UserOnline uo) {
        long expireTime = uo.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= TOKEN_REFRESH_THRESHOLD_MINUTES) {
            refreshToken(uo);
        }
    }

    /**
     * 刷新用户token时间并重设个人信息
     */
    public void refreshToken(UserOnline uo) {
        uo.setLoginTime(System.currentTimeMillis());
        uo.setExpireTime(uo.getLoginTime() + CacheConstants.TOKEN_EXPIRE_TIME * CacheConstants.MILLIS_MINUTE);

        String uoKey = TokenConstants.USER_TOKENS + uo.getToken();
        redisService.setCacheObject(uoKey, uo, CacheConstants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取当前登录用户的token
     */
    public String getUOToken(HttpServletRequest request) {
        return TokenUtils.getToken(request);
    }

    /**
     * 获取当前登录用户存储在redis中的key
     */
    public String getUOKey(String token) {
        return TokenConstants.USER_TOKENS + JWTService.getKey(JWTService.parseToken(token));
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
    public void delUserOnline(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String uoKey = getUOKey(token);
            redisService.deleteObject(uoKey);
        }
    }
}
