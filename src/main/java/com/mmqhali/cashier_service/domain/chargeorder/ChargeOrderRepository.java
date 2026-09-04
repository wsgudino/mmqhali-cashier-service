package com.mmqhali.cashier_service.domain.chargeorder;

import java.util.Optional;
import java.util.UUID;

/** Puerto de persistencia de la orden de cobro. Sin dependencia de Spring ni JPA (regla 9). */
public interface ChargeOrderRepository {

    ChargeOrder save(ChargeOrder chargeOrder);

    Optional<ChargeOrder> findById(UUID id);

    /** Si la misma atención llega dos veces, se devuelve la orden existente. */
    Optional<ChargeOrder> findByCareId(String careId);
}
