package com.mmqhali.cashier_service.application.chargeorder;

import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ChargeOrderResult(UUID chargeOrderId, String careId, String patientIdType, String patientIdNumber,
        String patientName, UUID invoiceRecipientId, String agreementRef, String status, Instant careDate,
        BigDecimal totalToCharge, List<ChargeOrderLineResult> lines) {

    static ChargeOrderResult from(ChargeOrder chargeOrder) {
        List<ChargeOrderLineResult> lines = chargeOrder.lines().stream()
                .map(ChargeOrderLineResult::from)
                .collect(Collectors.toList());
        return new ChargeOrderResult(chargeOrder.id(), chargeOrder.careId(),
                chargeOrder.patientIdentification().type().sriCode(), chargeOrder.patientIdentification().number(),
                chargeOrder.patientName(), chargeOrder.invoiceRecipientId(), chargeOrder.agreementRef(),
                chargeOrder.status().name(), chargeOrder.careDate(), chargeOrder.totalToCharge().value(), lines);
    }
}
