package com.mmqhali.cashier_service.infrastructure.web.payment;

import com.mmqhali.cashier_service.application.payment.AppliedPaymentInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AppliedPaymentRequest(@NotBlank String sriCode, @NotNull @Positive BigDecimal amount,
        String reference, String cardBank, String cardBrand, @PositiveOrZero int termDays,
        @NotBlank String timeUnit) {

    AppliedPaymentInput toInput() {
        return new AppliedPaymentInput(sriCode, amount, reference, cardBank, cardBrand, termDays, timeUnit);
    }
}
