package com.genius.framework.multitenancy.config;

import com.genius.framework.multitenancy.event.TenantCleanInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MultiTenantMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantCleanInterceptor()).addPathPatterns("/**");
    }
}
