package com.genius.framework.multitenancy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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
}
