package com.sql.file.config;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射配置
 * 将本地存储目录映射为可通过 HTTP 直接访问的 URL 路径
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    @Value("${file.avatar-path}")
    private String avatarPath;

    @Value("${file.sign-path}")
    private String signPath;

    @Value("${file.tp-path}")
    private String tpPath;

    @Value("${file.tm-path}")
    private String tmPath;

    @Value("${file.child-photo-path}")
    private String childPhotoPath;

    @Value("${file.path}")
    private String filePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 头像
        registry.addResourceHandler("/pics/avatars/**")
                .addResourceLocations("file:" + avatarPath + File.separator);
        // 签到/签退图片
        registry.addResourceHandler("/pics/signs/**")
                .addResourceLocations("file:" + signPath + File.separator);
        // 教案文件
        registry.addResourceHandler("/tps/**")
                .addResourceLocations("file:" + tpPath + File.separator);
        // 训练方法文件
        registry.addResourceHandler("/tms/**")
                .addResourceLocations("file:" + tmPath + File.separator);
        // 孩子照片
        registry.addResourceHandler("/pics/children/photos/**")
                .addResourceLocations("file:" + childPhotoPath + File.separator);
        // 通用文件
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + filePath + File.separator);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/pics/**").allowedOrigins("*").allowedMethods("GET");
        registry.addMapping("/tps/**").allowedOrigins("*").allowedMethods("GET");
        registry.addMapping("/tms/**").allowedOrigins("*").allowedMethods("GET");
        registry.addMapping("/files/**").allowedOrigins("*").allowedMethods("GET");
    }
}
