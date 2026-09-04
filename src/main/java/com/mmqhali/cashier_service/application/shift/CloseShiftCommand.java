package com.mmqhali.cashier_service.application.shift;

import java.math.BigDecimal;
import java.util.UUID;

public record CloseShiftCommand(UUID shiftId, BigDecimal cashCounted) {
}
