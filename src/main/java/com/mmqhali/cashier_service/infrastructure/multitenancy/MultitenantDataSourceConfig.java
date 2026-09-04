package com.mmqhali.cashier_service.infrastructure.multitenancy;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MultitenancyProperties.class)
public class MultitenantDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(MultitenantDataSourceConfig.class);

    @Bean
    public Map<String, DataSource> tenantDataSources(MultitenancyProperties properties) {
        Map<String, DataSource> dataSources = new HashMap<>();

        if (properties.tenants() == null || properties.tenants().isEmpty()) {
            throw new IllegalStateException("No hay empresas configuradas en app.multitenancy.tenants");
        }

        properties.tenants().forEach((tenantKey, tenantProps) -> {
            log.info("Configurando DataSource para empresa [{}]: {} ({})",
                    tenantKey, tenantProps.companyName(), tenantProps.url());

            HikariDataSource ds = new HikariDataSource();
            ds.setPoolName("HikariPool-" + tenantKey + "-" + tenantProps.companyName().replaceAll("\\s+", ""));
            ds.setJdbcUrl(tenantProps.url());
            ds.setUsername(tenantProps.username());
            ds.setPassword(tenantProps.password());
            if (tenantProps.driverClassName() != null && !tenantProps.driverClassName().isBlank()) {
                ds.setDriverClassName(tenantProps.driverClassName());
            }
            if (tenantProps.maximumPoolSize() != null && tenantProps.maximumPoolSize() > 0) {
                ds.setMaximumPoolSize(tenantProps.maximumPoolSize());
            }

            dataSources.put(tenantKey, ds);
        });

        return Collections.unmodifiableMap(dataSources);
    }

    @Bean
    @Primary
    public DataSource dataSource(MultitenancyProperties properties, Map<String, DataSource> tenantDataSources) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>(tenantDataSources);
        routingDataSource.setTargetDataSources(targetDataSources);

        String defaultTenant = properties.defaultTenant();
        DataSource defaultDs = tenantDataSources.get(defaultTenant);
        if (defaultDs == null) {
            defaultDs = tenantDataSources.values().iterator().next();
            log.warn("Empresa por defecto '{}' no encontrada. Usando primer DataSource disponible como fallback.", defaultTenant);
        } else {
            log.info("Empresa por defecto configurada: '{}'", defaultTenant);
        }
        routingDataSource.setDefaultTargetDataSource(defaultDs);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }
}
