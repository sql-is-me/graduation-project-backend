package com.sql.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sql.common.annotation.EnableCustomConfig;
import com.sql.common.annotation.EnableCustomFeignClients;

/**
 * 管理员服务模块
 */
@EnableCustomConfig
@EnableCustomFeignClients
@SpringBootApplication
public class AdminServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  管理员服务模块启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
