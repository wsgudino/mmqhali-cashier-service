package com.mmqhali.cashier_service.infrastructure.persistence.shared;

import com.mmqhali.cashier_service.domain.shared.Money;

import java.math.BigDecimal;

/** Conversor compartido entre {@link Money} y la columna numeric(14,2) — ver D19. */
public final class MoneyConverter {

    private MoneyConverter() {
    }

    public static BigDecimal toColumn(Money money) {
        return money == null ? null : money.value();
    }

    public static Money toDomain(BigDecimal column) {
        return column == null ? null : Money.of(column);
    }
}
