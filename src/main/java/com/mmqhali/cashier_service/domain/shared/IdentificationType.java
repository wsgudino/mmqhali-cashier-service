package com.mmqhali.cashier_service.domain.shared;

import com.mmqhali.cashier_service.domain.DomainException;

import java.util.Arrays;

/** Códigos del SRI. CONSUMIDOR_FINAL está acá porque Identification necesita poder rechazarlo. */
public enum IdentificationType {

    RUC("04"),
    CEDULA("05"),
    PASAPORTE("06"),
    CONSUMIDOR_FINAL("07"),
    EXTERIOR("08");

    private final String sriCode;

    IdentificationType(String sriCode) {
        this.sriCode = sriCode;
    }

    public String sriCode() {
        return sriCode;
    }

    public static IdentificationType fromSriCode(String sriCode) {
        return Arrays.stream(values())
                .filter(type -> type.sriCode.equals(sriCode))
                .findFirst()
                .orElseThrow(() -> new DomainException("Código de identificación SRI desconocido: " + sriCode));
    }
}
