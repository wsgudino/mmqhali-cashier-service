package com.mmqhali.cashier_service.infrastructure.persistence.shared;

import com.mmqhali.cashier_service.domain.shared.Identification;
import com.mmqhali.cashier_service.domain.shared.IdentificationType;

/** Conversor compartido entre {@link Identification} y el par de columnas (id_type, id_number) — ver D19. */
public final class IdentificationConverter {

    private IdentificationConverter() {
    }

    public static Identification toDomain(String typeColumn, String numberColumn) {
        return new Identification(IdentificationType.fromSriCode(typeColumn), numberColumn);
    }

    public static String typeColumn(Identification identification) {
        return identification.type().sriCode();
    }

    public static String numberColumn(Identification identification) {
        return identification.number();
    }
}
