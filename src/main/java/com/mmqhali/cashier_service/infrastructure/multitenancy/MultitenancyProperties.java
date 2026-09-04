package com.mmqhali.cashier_service.infrastructure.multitenancy;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "app.multitenancy")
public record MultitenancyProperties(
        String defaultTenant,
        String headerName,
        Map<String, TenantDataSourceProperties> tenants
) {
    public record TenantDataSourceProperties(
            String companyName,
            String url,
            String username,
            String password,
            String driverClassName,
            Integer maximumPoolSize
    ) {
    }
}
