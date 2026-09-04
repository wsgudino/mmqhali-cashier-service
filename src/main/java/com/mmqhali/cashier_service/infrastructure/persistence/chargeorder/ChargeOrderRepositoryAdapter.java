package com.mmqhali.cashier_service.infrastructure.persistence.chargeorder;

import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrder;
import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrderMapper;
import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class ChargeOrderRepositoryAdapter implements ChargeOrderRepository {

    private final SpringDataChargeOrderJpaRepository jpaRepository;

    ChargeOrderRepositoryAdapter(SpringDataChargeOrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ChargeOrder save(ChargeOrder chargeOrder) {
        return ChargeOrderMapper.toDomain(jpaRepository.save(ChargeOrderMapper.toEntity(chargeOrder)));
    }

    @Override
    public Optional<ChargeOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(ChargeOrderMapper::toDomain);
    }

    @Override
    public Optional<ChargeOrder> findByCareId(String careId) {
        return jpaRepository.findByCareId(careId).map(ChargeOrderMapper::toDomain);
    }
}
