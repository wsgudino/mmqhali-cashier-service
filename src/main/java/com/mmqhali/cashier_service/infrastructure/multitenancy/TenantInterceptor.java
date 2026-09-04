package com.mmqhali.cashier_service.infrastructure.multitenancy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interceptor HTTP que resuelve el código de empresa y lo fija en TenantContext.
 *
 * Cumple con la Regla 3 de CLAUDE.md:
 * "'codigoempresa' se resuelve del token, nunca del cuerpo de la petición.
 *  Ninguna firma de método de negocio lo recibe como parámetro."
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);
    private static final Pattern JWT_COMPANY_CODE_PATTERN = Pattern.compile("\"codigoempresa\"\\s*:\\s*\"([^\"]+)\"");

    private final MultitenancyProperties properties;

    public TenantInterceptor(MultitenancyProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String companyCode = resolveCompanyCode(request);

        if (companyCode == null || companyCode.isBlank()) {
            companyCode = properties.defaultTenant();
        }

        // Validar que la empresa resuelta exista en la configuración
        if (properties.tenants() != null && !properties.tenants().containsKey(companyCode)) {
            log.warn("Petición rechazada: Empresa '{}' no configurada en el servicio.", companyCode);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        TenantContext.setCurrentTenant(companyCode);
        log.debug("Petición enrutada a empresa: [{}]", companyCode);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }

    private String resolveCompanyCode(HttpServletRequest request) {
        // 1. Intentar resolver desde el Bearer JWT (prioridad 1, Regla 3)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            String codeFromJwt = extractCompanyCodeFromJwt(token);
            if (codeFromJwt != null) {
                return codeFromJwt;
            }
        }

        // 2. Intentar resolver desde encabezado configurado (fallback para pruebas directas/local)
        String headerName = properties.headerName();
        if (headerName != null && !headerName.isBlank()) {
            String codeFromHeader = request.getHeader(headerName);
            if (codeFromHeader != null && !codeFromHeader.isBlank()) {
                return codeFromHeader.trim();
            }
        }

        return null;
    }

    private String extractCompanyCodeFromJwt(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length >= 2) {
                byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
                String payload = new String(decodedBytes, StandardCharsets.UTF_8);
                Matcher matcher = JWT_COMPANY_CODE_PATTERN.matcher(payload);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            log.warn("No se pudo decodificar el claim 'codigoempresa' del token JWT: {}", e.getMessage());
        }
        return null;
    }
}
