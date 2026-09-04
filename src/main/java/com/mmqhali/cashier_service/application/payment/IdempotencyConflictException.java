package com.mmqhali.cashier_service.application.payment;

/** Misma clave de idempotencia, cuerpo distinto: no es un reintento, es un error del cliente. */
public class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String idempotencyKey) {
        super("La clave de idempotencia '" + idempotencyKey + "' ya se usó con una petición distinta");
    }
}
