package com.genius.framework.multitenancy.config;


import com.genius.framework.multitenancy.provider.MultiTenantContextProvider;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Component
@Order(2)
@ComponentScan(basePackageClasses = TenantConfig.class)
public class MultiTenantDataSourceConfig implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantDataSourceConfig.class);

    @Autowired
    private TenantConfig tenantConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 添加配置的租户
        Boolean multiTenancyEnabled = tenantConfig.isEnabled();
        if (multiTenancyEnabled) {
            List<Tenant> tenants = tenantConfig.getTenants();
            logger.info("Init tenant dataSource, tenants: {}", tenants.stream().map(Tenant::getName).collect(Collectors.toList()));
            for (Tenant tenant : tenants) {
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
}
