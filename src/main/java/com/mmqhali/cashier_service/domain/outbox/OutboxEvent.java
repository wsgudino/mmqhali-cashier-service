package com.mmqhali.cashier_service.domain.outbox;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Salida del tramo garantizado. Se escribe en la misma transacción que el hecho que representa
 * (ver Reglas-Desarrollo-Caja.md, sección ACID) — es lo que hace imposible que exista un cobro
 * sin su evento. {@link #markSent} devuelve una copia nueva en vez de mutar: el registro es un
 * valor, no una entidad con identidad de negocio propia más allá de su fila.
 */
public record OutboxEvent(UUID id, String aggregateType, UUID aggregateId, String eventType, String payload,
        Instant createdAt, Instant sentAt) {

    public OutboxEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(aggregateType, "aggregateType");
        Objects.requireNonNull(aggregateId, "aggregateId");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(payload, "payload");
        Objects.requireNonNull(createdAt, "createdAt");
    }

    public static OutboxEvent create(String aggregateType, UUID aggregateId, String eventType, String payload,
            Instant createdAt) {
        return new OutboxEvent(UUID.randomUUID(), aggregateType, aggregateId, eventType, payload, createdAt,
                null);
    }

    public OutboxEvent markSent(Instant sentAt) {
        return new OutboxEvent(id, aggregateType, aggregateId, eventType, payload, createdAt,
                Objects.requireNonNull(sentAt, "sentAt"));
    }

    public boolean isPending() {
        return sentAt == null;
    }
}
