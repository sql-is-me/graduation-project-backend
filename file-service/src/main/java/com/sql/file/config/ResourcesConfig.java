package com.sql.file.config;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通用映射配置
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {
        /**
         * 通用文件存储在本地的根路径
         */
        @Value("${file.path}")
        private String localFilePath;

        /**
         * 通用文件资源映射路径前缀
         */
        @Value("${file.prefix}")
        public String localFilePrefix;

        /**
         * 图片存储在本地的根路径
         */
        @Value("${file.pic-path}")
        private String localPicPath;

        /**
         * 图片资源映射路径前缀
         */
        @Value("${file.pic-prefix}")
        public String localPicPrefix;

        /**
         * 文档存储在本地的根路径
         */
        @Value("${file.doc-path}")
        private String localDocPath;

        /**
         * 文档资源映射路径前缀
         */
        @Value("${file.doc-prefix}")
        public String localDocPrefix;

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                /** 通用文件上传路径 */
                registry.addResourceHandler(localFilePrefix + "/**")
                                .addResourceLocations("file:" + localFilePath + File.separator);
                /** 本地图片上传路径 */
                registry.addResourceHandler(localPicPrefix + "/**")
                                .addResourceLocations("file:" + localPicPath + File.separator);
                /** 本地文档上传路径 */
                registry.addResourceHandler(localDocPrefix + "/**")
                                .addResourceLocations("file:" + localDocPath + File.separator);
        }

        /**
         * 开启跨域
         */
        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(localFilePrefix + "/**")
                                .allowedOrigins("*")
                                .allowedMethods("GET");
                registry.addMapping(localPicPrefix + "/**")
                                .allowedOrigins("*")
                                .allowedMethods("GET");
                registry.addMapping(localDocPrefix + "/**")
                                .allowedOrigins("*")
                                .allowedMethods("GET");
        }
}