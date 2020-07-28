package com.genius.framework.multitenancy.provider;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Component
public class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);

    /**
     * 初始化程序时，初始化当前的默认租户
     * @return
     */
    @Override
    protected DataSource selectAnyDataSource() {
        return MultiTenantContextProvider.getDefaultDataSource();
    }

    /**
     * 执行sql之前根据租户id获取对应的租户数据源
     * 注：在spring.jpa.open-in-view: true或默认时
     *    在一个方法内通过MultiTenantContextProvider.setCurrentTenant(XXX)多次设置当前的租户
     *    只能请求到一次这个方法，也就是说只有第一次切换数据源是生效的，后面的设置会被污染
     * @param tenantIdentifier
     * @return
     */
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        // 获取自定义设置的租户id，如果有自定义的租户id，则设置自定义的租户id，如果没有，则用从请求头里获取的租户id
        if (!StringUtils.isEmpty(MultiTenantContextProvider.getCurrentTenant())) {
            tenantIdentifier = MultiTenantContextProvider.getCurrentTenant();
        }
        DataSource tenantDataSource = MultiTenantContextProvider.getDataSourceByTenantId(tenantIdentifier);
        if (tenantDataSource == null) {
            logger.error("Can not find tenant by tenantId: {}, use default tenant.", tenantIdentifier);
            return MultiTenantContextProvider.getDefaultDataSource();
        }
        return tenantDataSource;
    }
}
