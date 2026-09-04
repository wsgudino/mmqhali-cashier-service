package com.mmqhali.cashier_service.application.chargeorder;

import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrderLine;

import java.math.BigDecimal;
import java.util.UUID;

public record ChargeOrderLineResult(UUID id, String serviceCode, int quantity, BigDecimal agreementRate,
        BigDecimal recognizedAmount, BigDecimal copago, BigDecimal discount, String vatRate, String status,
        String rejectionReason) {

    static ChargeOrderLineResult from(ChargeOrderLine line) {
        return new ChargeOrderLineResult(line.id(), line.serviceCode(), line.quantity(),
                line.agreementRate().value(), line.recognizedAmount().value(), line.copago().value(),
                line.discount().value(), line.vatRate().sriPercentageCode(), line.status().name(),
                line.rejectionReason());
    }
}
