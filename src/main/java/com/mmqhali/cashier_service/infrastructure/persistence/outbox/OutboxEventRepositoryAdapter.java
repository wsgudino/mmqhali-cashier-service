package com.mmqhali.cashier_service.infrastructure.persistence.outbox;

import com.mmqhali.cashier_service.domain.outbox.OutboxEvent;
import com.mmqhali.cashier_service.domain.outbox.OutboxEventRepository;
import org.springframework.stereotype.Repository;

@Repository
class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final SpringDataOutboxEventJpaRepository jpaRepository;

    OutboxEventRepositoryAdapter(SpringDataOutboxEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return OutboxEventMapper.toDomain(jpaRepository.save(OutboxEventMapper.toEntity(event)));
    }
}
