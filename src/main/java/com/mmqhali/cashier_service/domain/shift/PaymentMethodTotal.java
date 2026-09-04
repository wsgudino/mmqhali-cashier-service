package com.mmqhali.cashier_service.domain.shift;

import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shared.SriPaymentMethod;

import java.util.Objects;
import java.util.UUID;

/** Resumen del turno, una fila por forma de pago. Es una foto tomada al cerrar, no un acumulado. */
public record PaymentMethodTotal(UUID id, SriPaymentMethod method, Money total) {

    public PaymentMethodTotal {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(total, "total");
    }

    public static PaymentMethodTotal of(SriPaymentMethod method, Money total) {
        return new PaymentMethodTotal(UUID.randomUUID(), method, total);
    }
}
