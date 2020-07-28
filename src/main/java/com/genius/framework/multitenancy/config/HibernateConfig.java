package com.genius.framework.multitenancy.config;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Component
// 装配多租户包内的类
@ComponentScan(basePackages = {"com.genius.framework.multitenancy"})
public class HibernateConfig {
    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private PlatTenantConfig platTenantConfig;

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                       MultiTenantConnectionProvider multiTenantConnectionProviderImpl,
                                                                       CurrentTenantIdentifierResolver tenantIdentifierResolver) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.putAll(jpaProperties.getProperties());
        // 如果启用了多租户配置，则添加获取租户id和切换数据源的配置
        // TODO spring.jpa.open-in-view需要关闭，暂时没有找到好的解决办法
        if (platTenantConfig.getEnabled()) {
            properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
            properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProviderImpl);
            properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        }
        em.setDataSource(dataSource);
        // 扫描需要用到jpa的包
        em.setPackagesToScan("com.genius.framework");
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaPropertyMap(properties);
        return em;
    }
}
