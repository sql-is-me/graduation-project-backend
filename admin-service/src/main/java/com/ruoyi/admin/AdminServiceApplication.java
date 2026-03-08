package com.ruoyi.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

/**
 * 管理员服务模块
 */
@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class AdminServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  管理员服务模块启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
