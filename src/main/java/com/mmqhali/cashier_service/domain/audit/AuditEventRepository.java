package com.mmqhali.cashier_service.domain.audit;

/** Puerto de persistencia de la auditoría. Sin dependencia de Spring ni JPA (regla 9). */
public interface AuditEventRepository {

    AuditEvent save(AuditEvent event);
}
