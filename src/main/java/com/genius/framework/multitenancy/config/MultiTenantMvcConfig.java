package com.genius.framework.multitenancy.config;

import com.genius.framework.multitenancy.event.TenantCleanInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MultiTenantMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 匹配所有的请求，在已完成的时候清理租户id
        registry.addInterceptor(new TenantCleanInterceptor()).addPathPatterns("/**");
    }
}
