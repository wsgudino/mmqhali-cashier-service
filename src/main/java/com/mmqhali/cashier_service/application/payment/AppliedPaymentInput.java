package com.mmqhali.cashier_service.application.payment;

import java.math.BigDecimal;

/** Una forma de pago tal como la envía el cliente, todavía sin traducir a objetos de dominio. */
public record AppliedPaymentInput(String sriCode, BigDecimal amount, String reference, String cardBank,
        String cardBrand, int termDays, String timeUnit) {
}
