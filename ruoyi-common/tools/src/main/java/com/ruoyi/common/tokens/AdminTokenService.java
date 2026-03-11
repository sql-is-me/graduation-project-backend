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
import com.ruoyi.common.entity.Admin;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.redis.service.RedisService;

/**
 * token验证处理
 * 
 * @author ruoyi
 */
@Slf4j
@Component
public class AdminTokenService {
    protected static final long MILLIS_MINUTE = 60000;

    private final static long TOKEN_EXPIRE_TIME = 360;

    private final static Long TOKEN_REFRESH_THRESHOLD_MINUTES = CacheConstants.REFRESH_TIME * MILLIS_MINUTE;

    @Autowired
    private RedisService redisService;

    /**
     * 创建令牌
     * 管理员版本
     */
    public Map<String, Object> createToken(Admin admin) {
        String token = IdUtils.fastUUID();

        AdminOnline ao = new AdminOnline();
        ao.setAdminInfo(admin);
        ao.setIpaddr(IpUtils.getIpAddr());
        ao.setToken(token);
        refreshToken(ao);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put(JWTConstants.DETAILS_TOKEN, token);
        claimsMap.put(JWTConstants.DETAILS_ID, admin.getAdminId());
        claimsMap.put(JWTConstants.DETAILS_USERNAME, admin.getUsername());
        claimsMap.put(JWTConstants.DETAILS_TYPE, "0");

        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<String, Object>();
        rspMap.put("access_token", JWTService.createToken(claimsMap));
        rspMap.put("expires_in", TOKEN_EXPIRE_TIME);
        return rspMap;
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     */
    public void verifyToken(AdminOnline ao) {
        long expireTime = ao.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= TOKEN_REFRESH_THRESHOLD_MINUTES) {
            refreshToken(ao);
        }
    }

    /**
     * 刷新管理员token时间并重设个人信息
     *
     * @param ao adminOnline
     */
    public void refreshToken(AdminOnline ao) {
        ao.setLoginTime(System.currentTimeMillis());
        ao.setExpireTime(ao.getLoginTime() + TOKEN_EXPIRE_TIME * MILLIS_MINUTE);

        String aoKey = TokenConstants.TOKENS + ao.getToken();
        redisService.setCacheObject(aoKey, ao, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 获取当前登录管理员的token
     *
     * @return token
     */
    public String getAOToken(HttpServletRequest request) {
        return SecurityUtils.getToken(request);
    }

    /**
     * 获取当前登录管理员存储在redis中的key
     *
     * @return aoKey
     */
    public String getAOKey(String token) {
        return TokenConstants.TOKENS + JWTService.getKey(JWTService.parseToken(token));
    }

    /**
     * 获取管理员身份信息
     *
     * @param token JWT令牌
     * @return 用户信息
     */
    public AdminOnline getAO(String token) {
        try {
            String aoKey = getAOKey(token);
            return redisService.getCacheObject(aoKey);
        } catch (Exception e) {
            log.error("获取管理员信息异常'{}'", e.getMessage());
        }
        return null;
    }

    /**
     * 删除管理员缓存信息
     */
    public void delAdminOnline(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String aoKey = getAOKey(token);
            redisService.deleteObject(aoKey);
        }
    }
}