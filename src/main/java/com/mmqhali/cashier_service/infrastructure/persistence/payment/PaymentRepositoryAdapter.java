package com.mmqhali.cashier_service.infrastructure.persistence.payment;

import com.mmqhali.cashier_service.domain.payment.Payment;
import com.mmqhali.cashier_service.domain.payment.PaymentMapper;
import com.mmqhali.cashier_service.domain.payment.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class PaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpaRepository;

    PaymentRepositoryAdapter(SpringDataPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return PaymentMapper.toDomain(jpaRepository.save(PaymentMapper.toEntity(payment)));
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id).map(PaymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey).map(PaymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByChargeOrderId(UUID chargeOrderId) {
        return jpaRepository.findByChargeOrderId(chargeOrderId).map(PaymentMapper::toDomain);
    }

    @Override
    public List<Payment> findByShiftId(UUID shiftId) {
        return jpaRepository.findByShiftId(shiftId).stream().map(PaymentMapper::toDomain)
                .collect(Collectors.toList());
    }
}
