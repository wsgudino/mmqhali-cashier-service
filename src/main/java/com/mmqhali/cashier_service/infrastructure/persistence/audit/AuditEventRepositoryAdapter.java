package com.mmqhali.cashier_service.infrastructure.persistence.audit;

import com.mmqhali.cashier_service.domain.audit.AuditEvent;
import com.mmqhali.cashier_service.domain.audit.AuditEventRepository;
import org.springframework.stereotype.Repository;

@Repository
class AuditEventRepositoryAdapter implements AuditEventRepository {

    private final SpringDataAuditEventJpaRepository jpaRepository;

    AuditEventRepositoryAdapter(SpringDataAuditEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AuditEvent save(AuditEvent event) {
        return AuditEventMapper.toDomain(jpaRepository.save(AuditEventMapper.toEntity(event)));
    }
}
