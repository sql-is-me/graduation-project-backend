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
import com.sql.common.entity.AdminOnline;
import com.sql.common.entity.db.Admin;
import com.sql.common.redis.service.RedisService;
import com.sql.utils.IpUtils;
import com.sql.utils.uuid.IdUtils;

/**
 * token验证处理
 */
@Slf4j
@Component
public class AdminTokenService {

    private final static Long TOKEN_REFRESH_THRESHOLD_MINUTES = CacheConstants.REFRESH_TIME
            * CacheConstants.MILLIS_MINUTE;

    @Autowired
    private RedisService redisService;

    /**
     * 创建令牌
     * 管理员版本
     */
    public String createToken(Admin admin) {
        String token = IdUtils.fastUUID();

        AdminOnline ao = new AdminOnline();
        ao.setAdminInfo(admin);
        ao.setIpaddr(IpUtils.getIpAddr());
        ao.setToken(token);
        ao.setLoginTime(LocalDateTime.now().withNano(0));
        ao.setExpireTime(ao.getLoginTime().plusMinutes(CacheConstants.TOKEN_EXPIRE_TIME));

        checkAndDeleteCacheObject(admin.getAdminId());
        createAndSetCacheObject(ao);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put(JWTConstants.DETAILS_TOKEN, token);
        claimsMap.put(JWTConstants.DETAILS_ID, admin.getAdminId());
        claimsMap.put(JWTConstants.DETAILS_USERNAME, admin.getUsername());
        claimsMap.put(JWTConstants.DETAILS_TYPE, "0");

        // 接口返回信息
        String accessToken = JWTService.createToken(claimsMap);

        return accessToken;
    }

    /**
     * 若已经登录，则删除AO缓存对象，强制下线
     * 
     * 用于单点登录以及忘记密码
     */
    public void checkAndDeleteCacheObject(Long adminId) {
        String mappingKey = TokenConstants.ADMIN_TOKEN_MAPPING;
        String uuid = redisService.getCacheObject(mappingKey);
        if (uuid != null) {
            String aoKey = TokenConstants.ADMIN_TOKENS + uuid;
            redisService.deleteObject(aoKey);
            redisService.deleteObject(mappingKey);
        }
    }

    /**
     * 创建并设置缓存对象
     */
    public void createAndSetCacheObject(AdminOnline ao) {
        String aoKey = TokenConstants.ADMIN_TOKENS + ao.getToken();
        redisService.setCacheObject(aoKey, ao, CacheConstants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

        // 维护 adminId -> uuid 反向映射，用于强制下线
        String mappingKey = TokenConstants.ADMIN_TOKEN_MAPPING + ao.getAdminInfo().getAdminId();
        redisService.setCacheObject(mappingKey, ao.getToken(), CacheConstants.TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     */
    public void verifyToken(AdminOnline ao) {
        LocalDateTime expireTime = ao.getExpireTime();
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        if (expireTime.isAfter(currentTime)) {
            long minutesDiff = java.time.Duration.between(currentTime, expireTime).toMinutes();
            if (minutesDiff <= TOKEN_REFRESH_THRESHOLD_MINUTES) {
                resetExpireTime(ao);
            }
        }
    }

    /**
     * 刷新管理员token过期时间并重设个人信息
     *
     * @param ao adminOnline
     */
    public void resetExpireTime(AdminOnline ao) {
        ao.setExpireTime(ao.getLoginTime().plusMinutes(CacheConstants.TOKEN_EXPIRE_TIME));
        refreshCacheInfo(ao);
    }

    /**
     * 重设缓存中个人信息
     *
     * @param ao adminOnline
     */
    public void refreshCacheInfo(AdminOnline ao) {
        createAndSetCacheObject(ao);
    }

    /**
     * 获取当前登录管理员的access_token
     *
     * @return access_token
     */
    public String getAOToken(HttpServletRequest request) {
        return TokenUtils.getToken(request);
    }

    /**
     * 获取当前登录管理员存储在redis中的key
     *
     * @param token
     * @return aoKey
     */
    public String getAOKey(String token) {
        return TokenConstants.ADMIN_TOKENS + JWTService.getKey(JWTService.parseToken(token));
    }

    /**
     * 获取当前登录管理员存储在redis中的key
     *
     * @param ao
     * @return aoKey
     */
    public String getAOKey(AdminOnline ao) {
        return TokenConstants.ADMIN_TOKENS + ao.getToken();
    }

    /**
     * 获取管理员身份信息
     *
     * @param token access_token
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
     * 
     * @param access_token
     */
    public void delAdminCache(AdminOnline ao) {
        Long adminId = ao.getAdminInfo().getAdminId();

        // 删除ao缓存
        String aoKey = getAOKey(ao);
        redisService.deleteObject(aoKey);

        // 删除 adminId -> uuid 反向映射缓存
        String mappingKey = TokenConstants.ADMIN_TOKEN_MAPPING + adminId;
        redisService.deleteObject(mappingKey);
    }
}