package com.mmqhali.cashier_service.domain.payment;

import com.mmqhali.cashier_service.domain.shared.CardDetails;
import com.mmqhali.cashier_service.domain.shared.SriPaymentMethod;
import com.mmqhali.cashier_service.infrastructure.persistence.payment.AppliedPaymentJpaEntity;
import com.mmqhali.cashier_service.infrastructure.persistence.payment.PaymentJpaEntity;
import com.mmqhali.cashier_service.infrastructure.persistence.shared.CardDetailsConverter;
import com.mmqhali.cashier_service.infrastructure.persistence.shared.MoneyConverter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Traduce entre {@link Payment} y su entidad JPA (D19). Vive en el paquete del agregado porque
 * {@link Payment#reconstruct} es de paquete.
 */
public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentJpaEntity toEntity(Payment payment) {
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.setId(payment.id());
        entity.setChargeOrderId(payment.chargeOrderId());
        entity.setShiftId(payment.shiftId());
        entity.setIdempotencyKey(payment.idempotencyKey());
        entity.setChargedAt(payment.chargedAt());
        entity.setAppliedPayments(toAppliedPaymentEntities(payment.appliedPayments(), entity));
        return entity;
    }

    public static Payment toDomain(PaymentJpaEntity entity) {
        List<AppliedPayment> appliedPayments = entity.getAppliedPayments().stream()
                .map(PaymentMapper::toAppliedPaymentDomain)
                .collect(Collectors.toList());
        return Payment.reconstruct(entity.getId(), entity.getChargeOrderId(), entity.getShiftId(),
                entity.getIdempotencyKey(), appliedPayments, entity.getChargedAt());
    }

    private static List<AppliedPaymentJpaEntity> toAppliedPaymentEntities(List<AppliedPayment> appliedPayments,
            PaymentJpaEntity owner) {
        return appliedPayments.stream().map(applied -> {
            AppliedPaymentJpaEntity entity = new AppliedPaymentJpaEntity();
            entity.setId(applied.id());
            entity.setPayment(owner);
            entity.setSriCode(applied.method().sriCode());
            entity.setAmount(MoneyConverter.toColumn(applied.amount()));
            entity.setReference(applied.reference());
            entity.setCardBank(CardDetailsConverter.bankColumn(applied.cardDetails()));
            entity.setCardBrand(CardDetailsConverter.brandColumn(applied.cardDetails()));
            entity.setTermDays(applied.termDays());
            entity.setTimeUnit(applied.timeUnit());
            return entity;
        }).collect(Collectors.toList());
    }

    private static AppliedPayment toAppliedPaymentDomain(AppliedPaymentJpaEntity entity) {
        CardDetails cardDetails = CardDetailsConverter.toDomain(entity.getCardBank(), entity.getCardBrand());
        return new AppliedPayment(entity.getId(), SriPaymentMethod.fromSriCode(entity.getSriCode()),
                MoneyConverter.toDomain(entity.getAmount()), entity.getReference(), cardDetails,
                entity.getTermDays(), entity.getTimeUnit());
    }
}
