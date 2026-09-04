package com.mmqhali.cashier_service.infrastructure.persistence.outbox;

import com.mmqhali.cashier_service.domain.outbox.OutboxEvent;

/** Traduce entre {@link OutboxEvent} y su entidad JPA (D19). */
final class OutboxEventMapper {

    private OutboxEventMapper() {
    }

    static OutboxEventJpaEntity toEntity(OutboxEvent event) {
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.setId(event.id());
        entity.setAggregateType(event.aggregateType());
        entity.setAggregateId(event.aggregateId());
        entity.setEventType(event.eventType());
        entity.setPayload(event.payload());
        entity.setCreatedAt(event.createdAt());
        entity.setSentAt(event.sentAt());
        return entity;
    }

    static OutboxEvent toDomain(OutboxEventJpaEntity entity) {
        OutboxEvent event = new OutboxEvent(entity.getId(), entity.getAggregateType(), entity.getAggregateId(),
                entity.getEventType(), entity.getPayload(), entity.getCreatedAt(), null);
        return entity.getSentAt() == null ? event : event.markSent(entity.getSentAt());
    }
}
