package com.genius.framework.multitenancy.provider;

import com.zaxxer.hikari.HikariDataSource;
import com.genius.framework.multitenancy.constants.TenantConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Component
public class MultiTenantContextProvider {

    private static Logger logger = LoggerFactory.getLogger(MultiTenantContextProvider.class);

    /**
     * 存储当前自定义租户
     */
    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /**
     * 存储当前所有租户数据连接
     */
    private static Map<String, DataSource> dataSourceMap = new HashMap<>();

    private static final String YML_PATH_APPLICATION = "config/application.yml";
    private static final String YML_PATH_APPLICATION_DEV = "config/application-dev.yml";
    private static final String YML_PATH_APPLICATION_PROD = "config/application-prod.yml";

    public static String DEFAULT_DB_USERNAME = "";
    public static String DEFALUT_DB_PASSWORD = "";
    public static String DEFAULT_DB_URL = "";
    public static String DEFAULT_DB_PARAMS = "useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true&serverTimezone=UTC";



    public static DataSource getDefaultDataSource(){
        DataSource dataSource = dataSourceMap.get(TenantConstants.DEFAULT_TENANT_ID);
        if(dataSource == null){
            logger.info("Init default dataSource");
            try {
                YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
                yaml.setResources(new ClassPathResource(YML_PATH_APPLICATION));
                Properties properties = yaml.getObject();
                String active = properties.getProperty("spring.profiles.active");
                if ("prod".equals(active)) {
                    yaml.setResources(new ClassPathResource(YML_PATH_APPLICATION_PROD));
                    properties = yaml.getObject();
                } else {
                    yaml.setResources(new ClassPathResource(YML_PATH_APPLICATION_DEV));
                    properties = yaml.getObject();
                }
                String defaultDBUrl = properties.getProperty("spring.datasource.url");
                int ipStartIndex = defaultDBUrl.indexOf("//");
                int ipEndIndex = defaultDBUrl.lastIndexOf("/");
                if(ipStartIndex > -1 && ipEndIndex > ipStartIndex){
                    MultiTenantContextProvider.DEFAULT_DB_URL = defaultDBUrl.substring(ipStartIndex + 2, ipEndIndex);
                }
                MultiTenantContextProvider.DEFAULT_DB_USERNAME = properties.getProperty("spring.datasource.username");
                MultiTenantContextProvider.DEFALUT_DB_PASSWORD = properties.getProperty("spring.datasource.password");

                DataSource defaultDataSource = DataSourceBuilder.create()
                        .type(HikariDataSource.class)
                        .url(defaultDBUrl)
                        .username(DEFAULT_DB_USERNAME)
                        .password(DEFALUT_DB_PASSWORD)
                        .build();
                String maxPoolSize = properties.getProperty("spring.datasource.hikari.data-source-properties.maxPoolSize");
                if(!StringUtils.isEmpty(maxPoolSize)){
                    ((HikariDataSource) defaultDataSource).setMaximumPoolSize(Integer.parseInt(maxPoolSize));
                }
                MultiTenantContextProvider.setPlatTenant(TenantConstants.DEFAULT_TENANT_ID, defaultDataSource);
                return defaultDataSource;
            } catch (Exception e){
                logger.error("Init default dataSource error：{}", e.getMessage());
            }
        }
        return dataSourceMap.get(TenantConstants.DEFAULT_TENANT_ID);
    }


    public static DataSource getDataSourceByTenantId(String tenantId) {
        if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            return getDefaultDataSource();
        }
        return dataSourceMap.get(tenantId);
    }

    /**
     * 获取当前自定义租户
     * @return
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * 设置当前自定义租户
     * @param currentTenant
     */
    public static void setCurrentTenant(String currentTenant) {
        logger.info("Switch dataSource, tenant: {}", currentTenant);
        MultiTenantContextProvider.currentTenant.set(currentTenant);
    }

    /**
     * 清理当前自定义租户
     */
    public static void cleanCurrentTenant() {
        MultiTenantContextProvider.currentTenant.set(TenantConstants.DEFAULT_TENANT_ID);
    }

    /**
     * 获取当前所有的租户
     * @return
     */
    public static Set<String> getCurrentTenants() {
        return dataSourceMap.keySet();
    }

    public static void setPlatTenant(String tenantId, DataSource dataSource) {
        dataSourceMap.put(tenantId, dataSource);
    }
}
