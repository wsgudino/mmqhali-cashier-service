package com.mmqhali.cashier_service.infrastructure.web.chargeorder;

import com.mmqhali.cashier_service.application.chargeorder.LoadChargeOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record LoadChargeOrderRequest(@NotBlank String careId, @NotBlank String patientIdType,
        @NotBlank String patientIdNumber, String patientName, @NotBlank String recipientIdType,
        @NotBlank String recipientIdNumber, @NotBlank String recipientName, String recipientAddress,
        String recipientEmail, String recipientPhone, String agreementRef, Instant careDate,
        @NotEmpty List<@Valid ChargeOrderLineRequest> lines) {

    LoadChargeOrderCommand toCommand() {
        return new LoadChargeOrderCommand(careId, patientIdType, patientIdNumber, patientName, recipientIdType,
                recipientIdNumber, recipientName, recipientAddress, recipientEmail, recipientPhone, agreementRef,
                careDate, lines.stream().map(ChargeOrderLineRequest::toInput).collect(Collectors.toList()));
    }
}
