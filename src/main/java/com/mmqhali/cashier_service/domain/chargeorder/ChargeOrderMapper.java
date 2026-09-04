package com.mmqhali.cashier_service.domain.chargeorder;

import com.mmqhali.cashier_service.domain.shared.Identification;
import com.mmqhali.cashier_service.domain.shared.VatRate;
import com.mmqhali.cashier_service.infrastructure.persistence.chargeorder.ChargeOrderJpaEntity;
import com.mmqhali.cashier_service.infrastructure.persistence.chargeorder.ChargeOrderLineJpaEntity;
import com.mmqhali.cashier_service.infrastructure.persistence.shared.IdentificationConverter;
import com.mmqhali.cashier_service.infrastructure.persistence.shared.MoneyConverter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Traduce entre {@link ChargeOrder} y su entidad JPA (D19). Vive en el paquete del agregado
 * porque {@link ChargeOrder#reconstruct} es de paquete, igual que {@link ChargeOrderLine} no
 * necesita este trato: su constructor canónico ya es público (nunca muta).
 */
public final class ChargeOrderMapper {

    private ChargeOrderMapper() {
    }

    public static ChargeOrderJpaEntity toEntity(ChargeOrder chargeOrder) {
        ChargeOrderJpaEntity entity = new ChargeOrderJpaEntity();
        entity.setId(chargeOrder.id());
        entity.setCareId(chargeOrder.careId());
        entity.setPatientIdType(IdentificationConverter.typeColumn(chargeOrder.patientIdentification()));
        entity.setPatientIdNumber(IdentificationConverter.numberColumn(chargeOrder.patientIdentification()));
        entity.setPatientName(chargeOrder.patientName());
        entity.setInvoiceRecipientId(chargeOrder.invoiceRecipientId());
        entity.setAgreementRef(chargeOrder.agreementRef());
        entity.setStatus(chargeOrder.status().name());
        entity.setCareDate(chargeOrder.careDate());
        entity.setLines(toLineEntities(chargeOrder.lines(), entity));
        return entity;
    }

    public static ChargeOrder toDomain(ChargeOrderJpaEntity entity) {
        Identification patientIdentification = IdentificationConverter.toDomain(entity.getPatientIdType(),
                entity.getPatientIdNumber());
        List<ChargeOrderLine> lines = entity.getLines().stream()
                .map(ChargeOrderMapper::toLineDomain)
                .collect(Collectors.toList());
        return ChargeOrder.reconstruct(entity.getId(), entity.getCareId(), patientIdentification,
                entity.getPatientName(), entity.getInvoiceRecipientId(), entity.getAgreementRef(),
                ChargeOrder.Status.valueOf(entity.getStatus()), entity.getCareDate(), lines);
    }

    private static List<ChargeOrderLineJpaEntity> toLineEntities(List<ChargeOrderLine> lines,
            ChargeOrderJpaEntity owner) {
        return lines.stream().map(line -> {
            ChargeOrderLineJpaEntity entity = new ChargeOrderLineJpaEntity();
            entity.setId(line.id());
            entity.setChargeOrder(owner);
            entity.setServiceCode(line.serviceCode());
            entity.setQuantity(line.quantity());
            entity.setAgreementRate(MoneyConverter.toColumn(line.agreementRate()));
            entity.setRecognizedAmount(MoneyConverter.toColumn(line.recognizedAmount()));
            entity.setCopago(MoneyConverter.toColumn(line.copago()));
            entity.setDiscount(MoneyConverter.toColumn(line.discount()));
            entity.setVatRate(line.vatRate().name());
            entity.setStatus(line.status().name());
            entity.setRejectionReason(line.rejectionReason());
            return entity;
        }).collect(Collectors.toList());
    }

    private static ChargeOrderLine toLineDomain(ChargeOrderLineJpaEntity entity) {
        return new ChargeOrderLine(entity.getId(), entity.getServiceCode(), entity.getQuantity(),
                MoneyConverter.toDomain(entity.getAgreementRate()), MoneyConverter.toDomain(entity.getRecognizedAmount()),
                MoneyConverter.toDomain(entity.getCopago()), MoneyConverter.toDomain(entity.getDiscount()),
                VatRate.valueOf(entity.getVatRate()), ChargeOrderLine.Status.valueOf(entity.getStatus()),
                entity.getRejectionReason());
    }
}
