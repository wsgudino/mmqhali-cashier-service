package com.mmqhali.cashier_service.domain.invoicerecipient;

import com.mmqhali.cashier_service.domain.shared.Identification;

import java.util.Optional;
import java.util.UUID;

/** Puerto de persistencia del tercero a facturar. Sin dependencia de Spring ni JPA (regla 9). */
public interface InvoiceRecipientRepository {

    InvoiceRecipient save(InvoiceRecipient invoiceRecipient);

    Optional<InvoiceRecipient> findById(UUID id);

    /** Único por (id_type, id_number): antes de crear uno nuevo, se busca si ya existe. */
    Optional<InvoiceRecipient> findByIdentification(Identification identification);
}
