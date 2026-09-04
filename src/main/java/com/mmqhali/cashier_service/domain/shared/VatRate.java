package com.mmqhali.cashier_service.domain.shared;

import com.mmqhali.cashier_service.domain.DomainException;

import java.util.Arrays;

/** Salud va con ZERO, pero se factura igual y la línea debe aparecer. */
public enum VatRate {

    ZERO("0"),
    FIFTEEN("4");

    private final String sriPercentageCode;

    VatRate(String sriPercentageCode) {
        this.sriPercentageCode = sriPercentageCode;
    }

    public String sriPercentageCode() {
        return sriPercentageCode;
    }

    public static VatRate fromSriPercentageCode(String sriPercentageCode) {
        return Arrays.stream(values())
                .filter(rate -> rate.sriPercentageCode.equals(sriPercentageCode))
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        "Código de porcentaje de IVA del SRI desconocido: " + sriPercentageCode));
    }
}
