package com.mmqhali.cashier_service.infrastructure.multitenancy;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Ejecutor de migraciones Flyway independiente por cada base física de empresa.
 * Al arrancar el microservicio, garantiza que cada esquema de base de datos
 * tenga aplicadas las versiones vigentes (V1, V2, etc.).
 */
@Component
public class TenantFlywayRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantFlywayRunner.class);

    private final Map<String, DataSource> tenantDataSources;

    public TenantFlywayRunner(Map<String, DataSource> tenantDataSources) {
        this.tenantDataSources = tenantDataSources;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Iniciando migraciones Flyway multiempresa para {} base(s) de datos...", tenantDataSources.size());

        tenantDataSources.forEach((tenantKey, dataSource) -> {
            log.info("Ejecutando Flyway para empresa [{}]...", tenantKey);
            try {
                Flyway flyway = Flyway.configure()
                        .dataSource(dataSource)
                        .locations("classpath:db/migration")
                        .baselineOnMigrate(true)
                        .load();

                var result = flyway.migrate();
                log.info("Flyway finalizado exitosamente para empresa [{}]. Migraciones aplicadas: {}",
                        tenantKey, result.migrationsExecuted);
            } catch (Exception e) {
                log.error("Error al ejecutar migraciones Flyway para empresa [{}]: {}", tenantKey, e.getMessage(), e);
                throw new IllegalStateException("Fallo en la migración de base de datos para la empresa: " + tenantKey, e);
            }
        });

        log.info("Migraciones Flyway multiempresa completadas exitosamente.");
    }
}
