package com.mmqhali.cashier_service.infrastructure.persistence.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataAuditEventJpaRepository extends JpaRepository<AuditEventJpaEntity, UUID> {
}
