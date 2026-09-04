package com.mmqhali.cashier_service.domain;

/**
 * Raíz de las excepciones de dominio. Distinta de un fallo técnico: una regla de negocio
 * violada es un 400 con mensaje entendible, no un 500 con reintento.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
