package com.sql.user.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    RestTemplate restTemplate() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        BufferingClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(httpClient));

        RestTemplate template = new RestTemplate(factory);

        // 不将 Jackson 移到第 0 位，否则 Jackson 会拦截 String/byte[] 做 JSON 序列化
        // 在已有的 Jackson 转换器上追加 text/plain 支持即可（用于微信 jscode2session 响应）。
        for (HttpMessageConverter<?> converter : template.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                List<MediaType> types = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
                if (!types.contains(MediaType.TEXT_PLAIN)) {
                    types.add(MediaType.TEXT_PLAIN);
                    jacksonConverter.setSupportedMediaTypes(types);
                }
                break;
            }
        }

        template.getInterceptors().add(new LoggingInterceptor());
        return template;
    }

    static class LoggingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            log.info("=== HTTP REQUEST ===");
            log.info("URI    : {}", request.getURI());
            log.info("Method : {}", request.getMethod());
            log.info("Headers: {}", request.getHeaders());
            log.info("Body   : {}", new String(body, StandardCharsets.UTF_8));
            log.info("===================");
            return execution.execute(request, body);
        }
    }
}
