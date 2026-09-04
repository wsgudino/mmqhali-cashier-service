package com.mmqhali.cashier_service.domain.idempotency;

import java.util.Optional;

/** Puerto de persistencia de la idempotencia. Sin dependencia de Spring ni JPA (regla 9). */
public interface IdempotencyRecordRepository {

    IdempotencyRecord save(IdempotencyRecord record);

    Optional<IdempotencyRecord> findByKey(String key);
}
