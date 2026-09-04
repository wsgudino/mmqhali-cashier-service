package com.mmqhali.cashier_service.domain.shared;

import com.mmqhali.cashier_service.domain.DomainException;

import java.util.Objects;

/**
 * Rechaza el código de consumidor final al construirse. Si nunca puede existir un objeto
 * inválido, ningún servicio necesita acordarse de validarlo.
 */
public record Identification(IdentificationType type, String number) {

    public Identification {
        Objects.requireNonNull(type, "type");
        if (number == null || number.isBlank()) {
            throw new DomainException("El número de identificación es obligatorio");
        }
        if (type == IdentificationType.CONSUMIDOR_FINAL) {
            throw new DomainException("No se puede identificar como consumidor final");
        }
    }
}
