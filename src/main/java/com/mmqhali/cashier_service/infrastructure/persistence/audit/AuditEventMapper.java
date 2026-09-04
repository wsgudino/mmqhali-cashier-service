package com.mmqhali.cashier_service.infrastructure.persistence.audit;

import com.mmqhali.cashier_service.domain.audit.AuditEvent;

/** Traduce entre {@link AuditEvent} y su entidad JPA (D19). */
final class AuditEventMapper {

    private AuditEventMapper() {
    }

    static AuditEventJpaEntity toEntity(AuditEvent event) {
        AuditEventJpaEntity entity = new AuditEventJpaEntity();
        entity.setId(event.id());
        entity.setActor(event.actor());
        entity.setEventType(event.eventType());
        entity.setAggregateType(event.aggregateType());
        entity.setAggregateId(event.aggregateId());
        entity.setData(event.data());
        entity.setOccurredAt(event.occurredAt());
        return entity;
    }

    static AuditEvent toDomain(AuditEventJpaEntity entity) {
        return AuditEvent.create(entity.getActor(), entity.getEventType(), entity.getAggregateType(),
                entity.getAggregateId(), entity.getData(), entity.getOccurredAt());
    }
}
