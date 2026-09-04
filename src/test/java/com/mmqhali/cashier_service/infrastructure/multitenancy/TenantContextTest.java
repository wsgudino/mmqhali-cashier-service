package com.mmqhali.cashier_service.infrastructure.multitenancy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias de TenantContext")
class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Debe almacenar y recuperar el código de empresa actual en el hilo")
    void shouldSetAndGetCurrentTenant() {
        TenantContext.setCurrentTenant("01");
        assertThat(TenantContext.getCurrentTenant()).isEqualTo("01");
    }

    @Test
    @DisplayName("Debe limpiar el tenant actual al invocar clear")
    void shouldClearCurrentTenant() {
        TenantContext.setCurrentTenant("02");
        TenantContext.clear();
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }
}
