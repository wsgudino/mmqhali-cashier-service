package com.mmqhali.cashier_service.application.shift;

import com.mmqhali.cashier_service.domain.shift.PaymentMethodTotal;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentMethodTotalResult(UUID id, String sriCode, BigDecimal total) {

    static PaymentMethodTotalResult from(PaymentMethodTotal methodTotal) {
        return new PaymentMethodTotalResult(methodTotal.id(), methodTotal.method().sriCode(),
                methodTotal.total().value());
    }
}
