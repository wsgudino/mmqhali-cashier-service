package com.mmqhali.cashier_service.domain.invoicerecipient;

import com.mmqhali.cashier_service.infrastructure.persistence.invoicerecipient.InvoiceRecipientJpaEntity;
import com.mmqhali.cashier_service.infrastructure.persistence.shared.IdentificationConverter;

/**
 * Traduce entre {@link InvoiceRecipient} y su entidad JPA (D19). Vive en el paquete del
 * agregado porque {@link InvoiceRecipient#reconstruct} es de paquete.
 */
public final class InvoiceRecipientMapper {

    private InvoiceRecipientMapper() {
    }

    public static InvoiceRecipientJpaEntity toEntity(InvoiceRecipient invoiceRecipient) {
        InvoiceRecipientJpaEntity entity = new InvoiceRecipientJpaEntity();
        entity.setId(invoiceRecipient.id());
        entity.setIdType(IdentificationConverter.typeColumn(invoiceRecipient.identification()));
        entity.setIdNumber(IdentificationConverter.numberColumn(invoiceRecipient.identification()));
        entity.setName(invoiceRecipient.name());
        entity.setAddress(invoiceRecipient.address());
        entity.setEmail(invoiceRecipient.email());
        entity.setPhone(invoiceRecipient.phone());
        entity.setCreatedAt(invoiceRecipient.createdAt());
        entity.setUpdatedAt(invoiceRecipient.updatedAt());
        return entity;
    }

    public static InvoiceRecipient toDomain(InvoiceRecipientJpaEntity entity) {
        return InvoiceRecipient.reconstruct(entity.getId(),
                IdentificationConverter.toDomain(entity.getIdType(), entity.getIdNumber()), entity.getName(),
                entity.getAddress(), entity.getEmail(), entity.getPhone(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
