package com.mmqhali.cashier_service.domain.shared;

import com.mmqhali.cashier_service.domain.DomainException;

/** Banco emisor + marca. Vive en AppliedPayment solo cuando la forma de pago es tarjeta. */
public record CardDetails(String bank, CardBrand brand) {

    public CardDetails {
        if (bank == null || bank.isBlank()) {
            throw new DomainException("El banco emisor es obligatorio");
        }
        if (brand == null) {
            throw new DomainException("La marca de la tarjeta es obligatoria");
        }
    }
}
