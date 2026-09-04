package com.mmqhali.cashier_service.application.shift;

import com.mmqhali.cashier_service.domain.shift.Shift;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ShiftResult(UUID shiftId, String cashier, String branch, String establishment, String issuingPoint,
        BigDecimal openingFloat, BigDecimal cashCounted, BigDecimal difference, String status, Instant openedAt,
        Instant closedAt, List<PaymentMethodTotalResult> methodTotals) {

    public static ShiftResult from(Shift shift) {
        BigDecimal cashCounted = shift.cashCounted() == null ? null : shift.cashCounted().value();
        BigDecimal difference = shift.difference() == null ? null : shift.difference().value();
        List<PaymentMethodTotalResult> methodTotals = shift.methodTotals().stream()
                .map(PaymentMethodTotalResult::from)
                .collect(Collectors.toList());
        return new ShiftResult(shift.id(), shift.cashier(), shift.branch(), shift.establishment(),
                shift.issuingPoint(), shift.openingFloat().value(), cashCounted, difference, shift.status().name(),
                shift.openedAt(), shift.closedAt(), methodTotals);
    }
}
