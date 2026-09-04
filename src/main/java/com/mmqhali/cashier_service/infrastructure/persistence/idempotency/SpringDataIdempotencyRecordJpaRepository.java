package com.mmqhali.cashier_service.infrastructure.persistence.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataIdempotencyRecordJpaRepository extends JpaRepository<IdempotencyRecordJpaEntity, String> {
}
