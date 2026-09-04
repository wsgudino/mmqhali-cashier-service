package com.mmqhali.cashier_service.infrastructure.web.shift;

import com.mmqhali.cashier_service.application.shift.OpenShiftCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record OpenShiftRequest(@NotBlank String branch, @NotBlank String establishment,
        @NotBlank String issuingPoint, @NotNull @PositiveOrZero BigDecimal openingFloat) {

    OpenShiftCommand toCommand(String cashier) {
        return new OpenShiftCommand(cashier, branch, establishment, issuingPoint, openingFloat);
    }
}
