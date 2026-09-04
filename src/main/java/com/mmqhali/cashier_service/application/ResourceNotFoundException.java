package com.mmqhali.cashier_service.application;

/** Un identificador referenciado por el cliente no existe. Se traduce a 404, no a 400. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
