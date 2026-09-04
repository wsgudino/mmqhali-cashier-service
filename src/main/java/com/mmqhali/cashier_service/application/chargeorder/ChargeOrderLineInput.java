package com.mmqhali.cashier_service.application.chargeorder;

import java.math.BigDecimal;

/** Una línea tal como la ingresa el cajero a mano, todavía sin traducir a objetos de dominio. */
public record ChargeOrderLineInput(String serviceCode, int quantity, BigDecimal agreementRate,
        BigDecimal recognizedAmount, BigDecimal copago, BigDecimal discount, String vatRate, String status,
        String rejectionReason) {
}
