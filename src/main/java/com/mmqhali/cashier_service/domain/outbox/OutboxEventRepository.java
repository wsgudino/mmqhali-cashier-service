package com.mmqhali.cashier_service.domain.outbox;

/** Puerto de persistencia de la tabla de salida. Sin dependencia de Spring ni JPA (regla 9). */
public interface OutboxEventRepository {

    OutboxEvent save(OutboxEvent event);
}
