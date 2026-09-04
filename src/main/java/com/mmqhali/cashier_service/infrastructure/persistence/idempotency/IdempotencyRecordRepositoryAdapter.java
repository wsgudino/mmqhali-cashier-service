package com.mmqhali.cashier_service.infrastructure.persistence.idempotency;

import com.mmqhali.cashier_service.domain.idempotency.IdempotencyRecord;
import com.mmqhali.cashier_service.domain.idempotency.IdempotencyRecordRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class IdempotencyRecordRepositoryAdapter implements IdempotencyRecordRepository {

    private final SpringDataIdempotencyRecordJpaRepository jpaRepository;

    IdempotencyRecordRepositoryAdapter(SpringDataIdempotencyRecordJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public IdempotencyRecord save(IdempotencyRecord record) {
        return IdempotencyRecordMapper.toDomain(jpaRepository.save(IdempotencyRecordMapper.toEntity(record)));
    }

    @Override
    public Optional<IdempotencyRecord> findByKey(String key) {
        return jpaRepository.findById(key).map(IdempotencyRecordMapper::toDomain);
    }
}
