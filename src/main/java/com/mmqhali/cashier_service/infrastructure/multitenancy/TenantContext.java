package com.mmqhali.cashier_service.infrastructure.multitenancy;

/**
 * Contexto de hilo para almacenar el código de empresa activo en la petición.
 * Regla 3: 'codigoempresa' se resuelve del token, nunca del cuerpo de la petición.
 * Ninguna firma de método de dominio lo recibe como parámetro.
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
