package com.mmqhali.cashier_service.infrastructure.persistence.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataOutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
}
