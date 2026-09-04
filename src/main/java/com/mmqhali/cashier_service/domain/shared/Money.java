package com.mmqhali.cashier_service.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Todo importe pasa por acá: escala 2, HALF_UP. Única excepción del proyecto es
 * InvoiceLine.unitPrice (seis decimales), que no existe todavía porque la emisión está diferida.
 */
public record Money(BigDecimal value) {

    private static final int SCALE = 2;

    public Money {
        Objects.requireNonNull(value, "value");
        value = value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        return new Money(value.add(other.value));
    }

    public Money subtract(Money other) {
        return new Money(value.subtract(other.value));
    }

    public boolean isPositive() {
        return value.signum() > 0;
    }

    public boolean isNegative() {
        return value.signum() < 0;
    }

    public boolean isGreaterThan(Money other) {
        return value.compareTo(other.value) > 0;
    }
}
