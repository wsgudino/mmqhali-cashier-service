package com.mmqhali.cashier_service.domain.payment;

import com.mmqhali.cashier_service.domain.shared.CardDetails;
import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shared.SriPaymentMethod;

import java.util.Objects;
import java.util.UUID;

/**
 * Cada forma de pago usada en un cobro. Si es tarjeta (débito o crédito), exige
 * {@link CardDetails}; si no, lo prohíbe. Ninguno de los dos datos de tarjeta viaja al asiento
 * contable — eso solo importa acá, para conciliación.
 */
public record AppliedPayment(UUID id, SriPaymentMethod method, Money amount, String reference,
        CardDetails cardDetails, int termDays, String timeUnit) {

    public AppliedPayment {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(timeUnit, "timeUnit");
        if (amount == null || !amount.isPositive()) {
            throw new InvalidPayment("El monto de la forma de pago debe ser positivo");
        }
        if (method.isCard() && cardDetails == null) {
            throw new InvalidPayment("Una forma de pago con tarjeta exige banco emisor y marca");
        }
        if (!method.isCard() && cardDetails != null) {
            throw new InvalidPayment("Solo una forma de pago con tarjeta puede llevar datos de tarjeta");
        }
        if (termDays < 0) {
            throw new InvalidPayment("El plazo no puede ser negativo");
        }
    }

    public static AppliedPayment of(SriPaymentMethod method, Money amount, String reference,
            CardDetails cardDetails, int termDays, String timeUnit) {
        return new AppliedPayment(UUID.randomUUID(), method, amount, reference, cardDetails, termDays,
                timeUnit);
    }
}
