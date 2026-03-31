package com.sql.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.sql.common.annotation.EnableCustomConfig;
import com.sql.common.annotation.EnableCustomFeignClients;

/**
 * 用户/教练服务模块
 */
@EnableScheduling
@EnableCustomConfig
@EnableCustomFeignClients
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  用户/教练服务模块启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
