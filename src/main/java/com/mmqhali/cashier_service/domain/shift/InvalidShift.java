package com.mmqhali.cashier_service.domain.shift;

import com.mmqhali.cashier_service.domain.DomainException;

public final class InvalidShift extends DomainException {

    public InvalidShift(String message) {
        super(message);
    }
}
