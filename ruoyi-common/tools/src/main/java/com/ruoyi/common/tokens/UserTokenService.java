package com.ruoyi.common.tokens;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.Constants.JWTConstants;
import com.ruoyi.common.Constants.TokenConstants;
import com.ruoyi.common.JWT.JWTService;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.core.utils.uuid.IdUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.common.entity.User;
import com.ruoyi.common.entity.UserOnline;
import com.ruoyi.common.redis.service.RedisService;

/**
 * 用户/教练 token验证处理
 */
@Slf4j
@Component
public class UserTokenService {
    protected static final long MILLIS_MINUTE = 60000;

    private final static long TOKEN_EXPIRE_TIME = 360;

    private final static Long TOKEN_REFRESH_THRESHOLD_MINUTES = CacheConstants.REFRESH_TIME * MILLIS_MINUTE;

    /**
     * 用户token缓存前缀
     */
    private final static String USER_TOKENS = "user_tokens:";

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
        rspMap.put("expires_in", TOKEN_EXPIRE_TIME);
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
        uo.setExpireTime(uo.getLoginTime() + TOKEN_EXPIRE_TIME * MILLIS_MINUTE);

        String uoKey = USER_TOKENS + uo.getToken();
        redisService.setCacheObject(uoKey, uo, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取当前登录用户的token
     */
    public String getUOToken(HttpServletRequest request) {
        return SecurityUtils.getToken(request);
    }

    /**
     * 获取当前登录用户存储在redis中的key
     */
    public String getUOKey(String token) {
        return USER_TOKENS + JWTService.getKey(JWTService.parseToken(token));
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
