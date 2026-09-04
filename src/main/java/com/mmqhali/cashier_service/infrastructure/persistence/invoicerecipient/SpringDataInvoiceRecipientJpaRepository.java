package com.mmqhali.cashier_service.infrastructure.persistence.invoicerecipient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataInvoiceRecipientJpaRepository extends JpaRepository<InvoiceRecipientJpaEntity, UUID> {

    Optional<InvoiceRecipientJpaEntity> findByIdTypeAndIdNumber(String idType, String idNumber);
}
