package com.genius.framework.common.annotation;

import com.genius.framework.common.config.CommonFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 通用的feign client注解
 * feign client 之间调用传递请求头
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@FeignClient
public @interface CommonFeignClient {
    @AliasFor(annotation = FeignClient.class, attribute = "name")
    String name() default "";

    @AliasFor(annotation = FeignClient.class, attribute = "configuration")
    Class<?>[] configuration() default CommonFeignClientConfig.class;

    String url() default "";

    boolean decode404() default false;

    Class<?> fallback() default void.class;

    String path() default "";
}
