package com.mmqhali.cashier_service.domain.payment;

import java.util.Optional;
import java.util.UUID;

/** Puerto de persistencia del cobro. Sin dependencia de Spring ni JPA (regla 9). */
public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    /** La restricción única de la clave es la defensa real de la idempotencia (regla no negociable). */
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    /** Una orden de cobro tiene a lo sumo un pago (UNIQUE en charge_order_id). */
    Optional<Payment> findByChargeOrderId(UUID chargeOrderId);
}
