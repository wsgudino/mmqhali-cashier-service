package com.mmqhali.cashier_service.infrastructure.web.payment;

import com.mmqhali.cashier_service.application.payment.RegisterPaymentCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record RegisterPaymentRequest(@NotEmpty List<@Valid AppliedPaymentRequest> appliedPayments) {

    RegisterPaymentCommand toCommand(UUID chargeOrderId, UUID shiftId, String idempotencyKey) {
        return new RegisterPaymentCommand(chargeOrderId, shiftId, idempotencyKey,
                appliedPayments.stream().map(AppliedPaymentRequest::toInput).collect(Collectors.toList()));
    }
}
