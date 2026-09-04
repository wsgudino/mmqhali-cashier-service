package com.mmqhali.cashier_service.infrastructure.persistence.shared;

import com.mmqhali.cashier_service.domain.shared.CardBrand;
import com.mmqhali.cashier_service.domain.shared.CardDetails;

/** Conversor compartido entre {@link CardDetails} y el par de columnas (card_bank, card_brand) — ver D19. */
public final class CardDetailsConverter {

    private CardDetailsConverter() {
    }

    public static CardDetails toDomain(String bankColumn, String brandColumn) {
        if (bankColumn == null && brandColumn == null) {
            return null;
        }
        return new CardDetails(bankColumn, CardBrand.valueOf(brandColumn));
    }

    public static String bankColumn(CardDetails cardDetails) {
        return cardDetails == null ? null : cardDetails.bank();
    }

    public static String brandColumn(CardDetails cardDetails) {
        return cardDetails == null ? null : cardDetails.brand().name();
    }
}
