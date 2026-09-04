package com.mmqhali.cashier_service.domain.payment;

import com.mmqhali.cashier_service.domain.DomainException;

public final class InvalidPayment extends DomainException {

    public InvalidPayment(String message) {
        super(message);
    }
}
