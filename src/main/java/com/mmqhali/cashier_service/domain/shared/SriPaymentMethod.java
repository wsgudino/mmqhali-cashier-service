package com.mmqhali.cashier_service.domain.shared;

import com.mmqhali.cashier_service.domain.DomainException;

import java.util.Arrays;

/** Tabla 24 del SRI. Sabe cuáles usan el sistema financiero y cuáles son tarjeta. */
public enum SriPaymentMethod {

    CASH_NO_FINANCIAL_SYSTEM("01", false),
    DEBT_COMPENSATION("15", false),
    DEBIT_CARD("16", true),
    ELECTRONIC_MONEY("17", true),
    PREPAID_CARD("18", true),
    CREDIT_CARD("19", true),
    OTHER_FINANCIAL_SYSTEM("20", true),
    TITLE_ENDORSEMENT("21", true);

    private final String sriCode;
    private final boolean usesFinancialSystem;

    SriPaymentMethod(String sriCode, boolean usesFinancialSystem) {
        this.sriCode = sriCode;
        this.usesFinancialSystem = usesFinancialSystem;
    }

    public String sriCode() {
        return sriCode;
    }

    public boolean usesFinancialSystem() {
        return usesFinancialSystem;
    }

    public boolean isCard() {
        return this == DEBIT_CARD || this == CREDIT_CARD;
    }

    public static SriPaymentMethod fromSriCode(String sriCode) {
        return Arrays.stream(values())
                .filter(method -> method.sriCode.equals(sriCode))
                .findFirst()
                .orElseThrow(() -> new DomainException("Código de forma de pago SRI desconocido: " + sriCode));
    }
}
