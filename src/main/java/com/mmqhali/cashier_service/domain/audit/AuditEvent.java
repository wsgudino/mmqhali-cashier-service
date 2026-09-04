package com.mmqhali.cashier_service.domain.audit;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Trazabilidad financiera, no el log técnico. Inmutable: no tiene ni un solo método que cambie
 * un campo después de creado. Un registro de auditoría editable no sirve como auditoría — el
 * trigger de la migración V2 hace cumplir esto mismo a nivel de base.
 */
public record AuditEvent(UUID id, String actor, String eventType, String aggregateType, UUID aggregateId,
        String data, Instant occurredAt) {

    public AuditEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(aggregateType, "aggregateType");
        Objects.requireNonNull(aggregateId, "aggregateId");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public static AuditEvent create(String actor, String eventType, String aggregateType, UUID aggregateId,
            String data, Instant occurredAt) {
        return new AuditEvent(UUID.randomUUID(), actor, eventType, aggregateType, aggregateId, data,
                occurredAt);
    }
}
