package com.mmqhali.cashier_service.application.shift;

import java.math.BigDecimal;

/** El cajero viaja aparte del cuerpo: lo resuelve el controller de un header, no del JSON. */
public record OpenShiftCommand(String cashier, String branch, String establishment, String issuingPoint,
        BigDecimal openingFloat) {
}
