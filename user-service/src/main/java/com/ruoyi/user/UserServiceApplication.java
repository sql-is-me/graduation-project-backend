package com.ruoyi.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

/**
 * 用户/教练服务模块
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  用户/教练服务模块启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
