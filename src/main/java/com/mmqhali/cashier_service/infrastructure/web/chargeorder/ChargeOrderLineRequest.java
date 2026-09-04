package com.mmqhali.cashier_service.infrastructure.web.chargeorder;

import com.mmqhali.cashier_service.application.chargeorder.ChargeOrderLineInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ChargeOrderLineRequest(@NotBlank String serviceCode, @Positive int quantity,
        @NotNull @PositiveOrZero BigDecimal agreementRate, @NotNull @PositiveOrZero BigDecimal recognizedAmount,
        @NotNull @PositiveOrZero BigDecimal copago, @PositiveOrZero BigDecimal discount, @NotBlank String vatRate,
        @NotBlank String status, String rejectionReason) {

    ChargeOrderLineInput toInput() {
        BigDecimal discountOrZero = discount == null ? BigDecimal.ZERO : discount;
        return new ChargeOrderLineInput(serviceCode, quantity, agreementRate, recognizedAmount, copago,
                discountOrZero, vatRate, status, rejectionReason);
    }
}
