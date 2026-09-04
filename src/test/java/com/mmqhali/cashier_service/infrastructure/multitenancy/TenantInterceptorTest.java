package com.mmqhali.cashier_service.infrastructure.multitenancy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias de TenantInterceptor")
class TenantInterceptorTest {

    private TenantInterceptor interceptor;
    private MultitenancyProperties properties;

    @BeforeEach
    void setUp() {
        var tenants = Map.of(
                "01", new MultitenancyProperties.TenantDataSourceProperties(
                        "Ecuasanitas", "jdbc:postgresql://localhost:5432/db1", "user", "pass", null, 5),
                "02", new MultitenancyProperties.TenantDataSourceProperties(
                        "PraxMED", "jdbc:postgresql://localhost:5432/db2", "user", "pass", null, 5)
        );
        properties = new MultitenancyProperties("01", "X-Company-Code", tenants);
        interceptor = new TenantInterceptor(properties);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Regla 3: Debe resolver codigoempresa del claim del token JWT")
    void shouldExtractCompanyCodeFromJwt() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String payload = "{\"sub\":\"cajero1\",\"codigoempresa\":\"02\"}";
        String base64Payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String dummyJwt = "header." + base64Payload + ".signature";

        request.addHeader("Authorization", "Bearer " + dummyJwt);

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        assertThat(TenantContext.getCurrentTenant()).isEqualTo("02");
    }

    @Test
    @DisplayName("Debe resolver codigoempresa del encabezado configurado como fallback")
    void shouldExtractCompanyCodeFromHeaderFallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("X-Company-Code", "02");

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        assertThat(TenantContext.getCurrentTenant()).isEqualTo("02");
    }

    @Test
    @DisplayName("Debe usar la empresa por defecto si no viene token ni encabezado")
    void shouldUseDefaultTenantWhenNoHeaderOrToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        assertThat(TenantContext.getCurrentTenant()).isEqualTo("01");
    }

    @Test
    @DisplayName("Debe rechazar con HTTP 400 si la empresa no existe en la configuración")
    void shouldRejectWhenTenantNotConfigured() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("X-Company-Code", "99"); // No configurada

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isFalse();
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }

    @Test
    @DisplayName("Debe limpiar el TenantContext al completar la petición (afterCompletion)")
    void shouldClearTenantContextAfterCompletion() {
        TenantContext.setCurrentTenant("01");
        interceptor.afterCompletion(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object(), null);

        assertThat(TenantContext.getCurrentTenant()).isNull();
    }
}
