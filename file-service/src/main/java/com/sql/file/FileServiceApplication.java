package com.sql.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 文件服务启动程序
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class FileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
        System.out.println("==================================" + "\n" +
                "       文件服务模块启动成功" + "\n"
                + "==================================");
    }
}
