package com.mmqhali.cashier_service.domain.chargeorder;

import com.mmqhali.cashier_service.domain.DomainException;

public final class InvalidChargeOrder extends DomainException {

    public InvalidChargeOrder(String message) {
        super(message);
    }
}
