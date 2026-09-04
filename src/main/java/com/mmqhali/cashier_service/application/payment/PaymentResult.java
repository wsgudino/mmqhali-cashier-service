package com.mmqhali.cashier_service.application.payment;

import com.mmqhali.cashier_service.domain.payment.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** Resultado de un cobro registrado. Es lo que se cachea para reintentos idempotentes. */
public record PaymentResult(UUID paymentId, UUID chargeOrderId, UUID shiftId, BigDecimal totalCharged,
        Instant chargedAt, List<AppliedPaymentResult> appliedPayments) {

    public static PaymentResult from(Payment payment) {
        BigDecimal total = payment.appliedPayments().stream()
                .map(applied -> applied.amount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<AppliedPaymentResult> applied = payment.appliedPayments().stream()
                .map(AppliedPaymentResult::from)
                .collect(Collectors.toList());
        return new PaymentResult(payment.id(), payment.chargeOrderId(), payment.shiftId(), total,
                payment.chargedAt(), applied);
    }
}
