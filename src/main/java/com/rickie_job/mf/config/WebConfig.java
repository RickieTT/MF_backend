package com.rickie_job.mf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author : Colin Xu
 * @Date : 2023/3/8 21:32
 * @Desc : Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Value("${server.servlet.context-path}")
    String contextPath;

    /**
     * 添加Web项目的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 对所有访问路径，都通过AuthInterceptor类型的拦截器进行拦截
        registry.addInterceptor(getAuthInterceptor()).addPathPatterns("/**")
                .excludePathPatterns(contextPath + "/user/login");
    }

    @Bean
    public AuthInterceptor getAuthInterceptor() {
        return new AuthInterceptor();
    }

}
