package com.mmqhali.cashier_service.infrastructure.persistence.invoicerecipient;

import com.mmqhali.cashier_service.domain.invoicerecipient.InvoiceRecipient;
import com.mmqhali.cashier_service.domain.invoicerecipient.InvoiceRecipientMapper;
import com.mmqhali.cashier_service.domain.invoicerecipient.InvoiceRecipientRepository;
import com.mmqhali.cashier_service.domain.shared.Identification;
import com.mmqhali.cashier_service.infrastructure.persistence.shared.IdentificationConverter;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class InvoiceRecipientRepositoryAdapter implements InvoiceRecipientRepository {

    private final SpringDataInvoiceRecipientJpaRepository jpaRepository;

    InvoiceRecipientRepositoryAdapter(SpringDataInvoiceRecipientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public InvoiceRecipient save(InvoiceRecipient invoiceRecipient) {
        return InvoiceRecipientMapper.toDomain(jpaRepository.save(InvoiceRecipientMapper.toEntity(invoiceRecipient)));
    }

    @Override
    public Optional<InvoiceRecipient> findById(UUID id) {
        return jpaRepository.findById(id).map(InvoiceRecipientMapper::toDomain);
    }

    @Override
    public Optional<InvoiceRecipient> findByIdentification(Identification identification) {
        return jpaRepository.findByIdTypeAndIdNumber(IdentificationConverter.typeColumn(identification),
                IdentificationConverter.numberColumn(identification)).map(InvoiceRecipientMapper::toDomain);
    }
}
