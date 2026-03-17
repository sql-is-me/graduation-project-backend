package com.sql.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import com.sql.common.config.FeignConfiguration;
import com.sql.common.config.ApplicationConfig;
import com.sql.common.handler.GlobalExceptionHandler;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.sql.**.mapper")
// 开启线程异步执行
@EnableAsync
// 自动加载类
@Import({ ApplicationConfig.class, FeignConfiguration.class, GlobalExceptionHandler.class })
public @interface EnableCustomConfig {

}
