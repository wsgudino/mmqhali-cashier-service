package com.mmqhali.cashier_service.domain.invoicerecipient;

import com.mmqhali.cashier_service.domain.DomainException;

public final class InvalidInvoiceRecipient extends DomainException {

    public InvalidInvoiceRecipient(String message) {
        super(message);
    }
}
