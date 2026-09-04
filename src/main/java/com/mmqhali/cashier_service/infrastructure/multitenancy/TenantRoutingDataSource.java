package com.mmqhali.cashier_service.infrastructure.multitenancy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Enrutador dinámico de DataSource según el tenant activo en TenantContext.
 */
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }
}
