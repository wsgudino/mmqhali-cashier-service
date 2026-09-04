package com.mmqhali.cashier_service.application.payment;

import java.util.List;
import java.util.UUID;

/** Intención de registrar un cobro. La clave de idempotencia viaja aparte del cuerpo (header HTTP). */
public record RegisterPaymentCommand(UUID chargeOrderId, UUID shiftId, String idempotencyKey,
        List<AppliedPaymentInput> appliedPayments) {
}
