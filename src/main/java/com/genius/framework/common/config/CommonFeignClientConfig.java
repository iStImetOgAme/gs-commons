package com.genius.framework.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * feign client 之间的调用传递请求头的注解配置类
 */
public class CommonFeignClientConfig {

    @Bean(name = "commonFeignClientInterceptor")
    public RequestInterceptor getCommonFeignClientInterceptor() {
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if (headerNames != null) {
                        String headerName = "";
                        while (headerNames.hasMoreElements()) {
                            headerName = headerNames.nextElement();
                            template.header(headerName, request.getHeader(headerName));
                        }
                    }
                }
            }
        };
    }
}
