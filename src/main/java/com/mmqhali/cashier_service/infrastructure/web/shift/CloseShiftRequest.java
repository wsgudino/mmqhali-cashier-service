package com.mmqhali.cashier_service.infrastructure.web.shift;

import com.mmqhali.cashier_service.application.shift.CloseShiftCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record CloseShiftRequest(@NotNull @PositiveOrZero BigDecimal cashCounted) {

    CloseShiftCommand toCommand(UUID shiftId) {
        return new CloseShiftCommand(shiftId, cashCounted);
    }
}
