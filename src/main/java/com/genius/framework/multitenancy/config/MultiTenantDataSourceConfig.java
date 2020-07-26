package com.genius.framework.multitenancy.config;


import com.genius.framework.multitenancy.provider.MultiTenantContextProvider;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@Component
@Order(2)
@ComponentScan(basePackageClasses = PlatTenantConfig.class)
public class MultiTenantDataSourceConfig implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantDataSourceConfig.class);

    private static final String YML_PATH_APPLICATION = "config/application.yml";

    private static final String YML_PATH_MULTITENANCY_DEV = "config/multitenancy-dev.yml";

    private static final String YML_PATH_MULTITENANCY_PROD = "config/multitenancy-prod.yml";

    @Autowired
    private PlatTenantConfig platTenantConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 添加配置的租户
        Boolean multiTenancyEnabled = platTenantConfig.getEnabled();
        if (multiTenancyEnabled) {
            List<PlatTenant> tenants = platTenantConfig.getTenants();
            logger.info("Init tenant dataSource, tenants: {}", tenants.stream().map(PlatTenant::getName).collect(Collectors.toList()));
            for (PlatTenant tenant : tenants) {
                try {
                    DataSource tenantDataSource = DataSourceBuilder.create()
                            .type(HikariDataSource.class)
                            .url(tenant.getUrl())
                            .username(tenant.getUsername())
                            .password(tenant.getPassword())
                            .build();
                    MultiTenantContextProvider.setPlatTenant(tenant.getId(), tenantDataSource);
                }catch (Exception e){
                    logger.error("Init tenant dataSource error, tenantId: {}, msg：{}", tenant.getId(), e);
                }
            }
        }
    }

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
