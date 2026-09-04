package com.mmqhali.cashier_service.infrastructure.persistence.idempotency;

import com.mmqhali.cashier_service.domain.idempotency.IdempotencyRecord;

/** Traduce entre {@link IdempotencyRecord} y su entidad JPA (D19). */
final class IdempotencyRecordMapper {

    private IdempotencyRecordMapper() {
    }

    static IdempotencyRecordJpaEntity toEntity(IdempotencyRecord record) {
        IdempotencyRecordJpaEntity entity = new IdempotencyRecordJpaEntity();
        entity.setKey(record.key());
        entity.setRequestHash(record.requestHash());
        entity.setResult(record.result());
        entity.setCreatedAt(record.createdAt());
        entity.setExpiresAt(record.expiresAt());
        return entity;
    }

    static IdempotencyRecord toDomain(IdempotencyRecordJpaEntity entity) {
        return IdempotencyRecord.create(entity.getKey(), entity.getRequestHash(), entity.getResult(),
                entity.getCreatedAt(), entity.getExpiresAt());
    }
}
