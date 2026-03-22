package com.sql.file.config;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sql.file.filter.RefererFilter;

/**
 * Filter配置
 */
@Configuration
public class FilterConfig {
    @Value("${file.prefix}")
    public String localFilePrefix;

    @Value("${file.pic-prefix}")
    public String localPicPrefix;

    @Value("${file.doc-prefix}")
    public String localDocPrefix;

    @Value("${referer.allowed-domains}")
    private String allowedDomains;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    @ConditionalOnProperty(value = "referer.enabled", havingValue = "true")
    public FilterRegistrationBean refererFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new RefererFilter());
        registration.addUrlPatterns(localFilePrefix + "/*", localPicPrefix + "/*", localDocPrefix + "/*");
        registration.setName("refererFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("allowedDomains", allowedDomains);
        registration.setInitParameters(initParameters);
        return registration;
    }
}
