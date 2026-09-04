package com.mmqhali.cashier_service.application.payment;

import com.mmqhali.cashier_service.domain.payment.AppliedPayment;

import java.math.BigDecimal;
import java.util.UUID;

public record AppliedPaymentResult(UUID id, String sriCode, BigDecimal amount, String reference, int termDays,
        String timeUnit) {

    static AppliedPaymentResult from(AppliedPayment appliedPayment) {
        return new AppliedPaymentResult(appliedPayment.id(), appliedPayment.method().sriCode(),
                appliedPayment.amount().value(), appliedPayment.reference(), appliedPayment.termDays(),
                appliedPayment.timeUnit());
    }
}
