package com.ruoyi.common.verifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.entity.Admin;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.redis.service.RedisService;

public class PWCheckUtils {
    private final static String PWD_ERR_CNT_KEY = "pwd_err_ctr:";

    private final static Integer PASSWORD_MAX_RETRY_COUNT = 3;

    private final static Long PASSWORD_LOCK_TIME = 5L;

    private final static BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private RedisService redisService;

    /**
     * 登录账户密码错误次数缓存键名
     *
     */
    private String getCacheKey(String username) {
        return PWD_ERR_CNT_KEY + username;
    }

    /**
     * 密码校验
     */
    public void validate(Admin admin, String password) {
        String username = admin.getUsername();

        Integer retryCounter = redisService.getCacheObject(getCacheKey(username));
        if (retryCounter == null) {
            retryCounter = 0;
        }

        if (retryCounter >= PASSWORD_MAX_RETRY_COUNT) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", PASSWORD_MAX_RETRY_COUNT, PASSWORD_LOCK_TIME);
            throw new ServiceException(errMsg);
        }

        if (!matches(admin, password)) {
            retryCounter++;

            redisService.setCacheObject(getCacheKey(username), retryCounter,
                    PASSWORD_LOCK_TIME, TimeUnit.MINUTES);
            throw new ServiceException("用户不存在/密码错误");
        } else {
            if (redisService.hasKey(getCacheKey(username))) {
                redisService.deleteObject(getCacheKey(username));
            }
        }
    }

    public boolean matches(Admin admin, String rawPassword) {
        return ENCODER.matches(rawPassword, admin.getPassword());
    }
}
