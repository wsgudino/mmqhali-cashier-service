package com.mmqhali.cashier_service.infrastructure.multitenancy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias de TenantRoutingDataSource")
class TenantRoutingDataSourceTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("determineCurrentLookupKey debe devolver el tenant fijado en TenantContext")
    void shouldReturnTenantFromContext() throws Exception {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        TenantContext.setCurrentTenant("01");

        Method method = TenantRoutingDataSource.class.getDeclaredMethod("determineCurrentLookupKey");
        method.setAccessible(true);
        Object key = method.invoke(routingDataSource);

        assertThat(key).isEqualTo("01");
    }
}
