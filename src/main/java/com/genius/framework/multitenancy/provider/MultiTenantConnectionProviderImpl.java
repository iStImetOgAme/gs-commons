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

    @Override
    protected DataSource selectAnyDataSource() {
        return MultiTenantContextProvider.getDefaultDataSource();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
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
