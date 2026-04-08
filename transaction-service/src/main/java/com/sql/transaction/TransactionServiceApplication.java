package com.sql.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sql.common.annotation.EnableCustomConfig;
import com.sql.common.annotation.EnableCustomFeignClients;

/**
 * 交易服务模块
 */
@EnableCustomConfig
@EnableCustomFeignClients
@SpringBootApplication
public class TransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
        System.out.println("==================================" + "\n" +
                "       交易服务模块启动成功" + "\n"
                + "==================================");
    }
}
