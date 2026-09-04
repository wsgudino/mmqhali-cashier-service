package com.mmqhali.cashier_service.domain.idempotency;

import com.mmqhali.cashier_service.domain.DomainException;

import java.time.Instant;
import java.util.Objects;

/**
 * Guarda clave, huella de la petición, resultado y vencimiento. La restricción única en base es
 * la defensa real; esto es lo que la acompaña en memoria. La huella detecta el caso peligroso:
 * misma clave, cuerpo distinto — eso no es un reintento, es un error del cliente.
 */
public record IdempotencyRecord(String key, String requestHash, String result, Instant createdAt,
        Instant expiresAt) {

    public IdempotencyRecord {
        if (key == null || key.isBlank()) {
            throw new DomainException("La clave de idempotencia es obligatoria");
        }
        Objects.requireNonNull(requestHash, "requestHash");
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(expiresAt, "expiresAt");
    }

    public static IdempotencyRecord create(String key, String requestHash, String result, Instant createdAt,
            Instant expiresAt) {
        return new IdempotencyRecord(key, requestHash, result, createdAt, expiresAt);
    }

    public boolean matchesRequest(String candidateHash) {
        return requestHash.equals(candidateHash);
    }
}
