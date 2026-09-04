package com.mmqhali.cashier_service.application.shift;

import com.mmqhali.cashier_service.domain.shift.Shift;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ShiftResult(UUID shiftId, String cashier, String branch, String establishment, String issuingPoint,
        BigDecimal openingFloat, String status, Instant openedAt) {

    public static ShiftResult from(Shift shift) {
        return new ShiftResult(shift.id(), shift.cashier(), shift.branch(), shift.establishment(),
                shift.issuingPoint(), shift.openingFloat().value(), shift.status().name(), shift.openedAt());
    }
}
