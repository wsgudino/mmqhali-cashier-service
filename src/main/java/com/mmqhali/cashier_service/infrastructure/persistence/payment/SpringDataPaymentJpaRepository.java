package com.mmqhali.cashier_service.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataPaymentJpaRepository extends JpaRepository<PaymentJpaEntity, UUID> {

    Optional<PaymentJpaEntity> findByIdempotencyKey(String idempotencyKey);

    Optional<PaymentJpaEntity> findByChargeOrderId(UUID chargeOrderId);

    List<PaymentJpaEntity> findByShiftId(UUID shiftId);
}
