package com.genius.framework.multitenancy.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 租户信息的配置类
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "multitenancy-platform")
public class PlatTenantConfig {

    Boolean enabled = false;
    List<PlatTenant> tenants = new ArrayList<>();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<PlatTenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<PlatTenant> tenants) {
        this.tenants = tenants;
    }


    private static final String YML_PATH_APPLICATION = "config/application.yml";

    private static final String YML_PATH_MULTITENANCY_DEV = "config/multitenancy-dev.yml";

    private static final String YML_PATH_MULTITENANCY_PROD = "config/multitenancy-prod.yml";

    /**
     * 读取租户配置文件
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(YML_PATH_APPLICATION));
        Properties properties = yaml.getObject();
        String active = properties.getProperty("spring.profiles.active");
        if("prod".equals(active)){
            yaml.setResources(new ClassPathResource(YML_PATH_MULTITENANCY_PROD));
        }else{
            yaml.setResources(new ClassPathResource(YML_PATH_MULTITENANCY_DEV));
        }
        configurer.setProperties(yaml.getObject());
        return configurer;
    }
}
