package com.mmqhali.cashier_service.domain.payment;

import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrder;
import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shared.SriPaymentMethod;
import com.mmqhali.cashier_service.domain.shift.Shift;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * El dinero entrando. Se construye por método de fábrica, no por constructor: así es imposible
 * tener en memoria un cobro que no pasó por las validaciones.
 *
 * <p>{@link #register} recibe el estado del turno y de la orden como valores, no las clases
 * completas — Payment no las custodia, solo verifica sus propias reglas contra esos hechos.
 * {@link #reconstruct}, de paquete, es para el mapeador: un cobro de ayer no vuelve a pasar por
 * la validación de bancarización o de cuadre, ya pasó una vez.
 */
public final class Payment {

    /** Umbral de bancarización del SRI: sobre este monto no se admite el código 01. */
    public static final Money BANKING_THRESHOLD = Money.of(new BigDecimal("500.00"));

    private final UUID id;
    private final UUID chargeOrderId;
    private final UUID shiftId;
    private final String idempotencyKey;
    private final List<AppliedPayment> appliedPayments;
    private final Instant chargedAt;

    private Payment(UUID id, UUID chargeOrderId, UUID shiftId, String idempotencyKey,
            List<AppliedPayment> appliedPayments, Instant chargedAt) {
        this.id = id;
        this.chargeOrderId = chargeOrderId;
        this.shiftId = shiftId;
        this.idempotencyKey = idempotencyKey;
        this.appliedPayments = new ArrayList<>(appliedPayments);
        this.chargedAt = chargedAt;
    }

    public static Payment register(UUID chargeOrderId, ChargeOrder.Status chargeOrderStatus, Money orderTotal,
            UUID shiftId, Shift.Status shiftStatus, String idempotencyKey,
            List<AppliedPayment> appliedPayments, Instant chargedAt) {
        Objects.requireNonNull(chargeOrderId, "chargeOrderId");
        Objects.requireNonNull(shiftId, "shiftId");
        Objects.requireNonNull(orderTotal, "orderTotal");
        Objects.requireNonNull(chargedAt, "chargedAt");
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidPayment("La clave de idempotencia es obligatoria");
        }
        if (shiftStatus != Shift.Status.OPEN) {
            throw new InvalidPayment("No se puede cobrar contra un turno cerrado");
        }
        if (chargeOrderStatus != ChargeOrder.Status.PRICED) {
            throw new InvalidPayment("No se puede cobrar una orden que no esté valorizada");
        }
        if (appliedPayments == null || appliedPayments.isEmpty()) {
            throw new InvalidPayment("El cobro exige al menos una forma de pago");
        }
        Money sum = appliedPayments.stream().map(AppliedPayment::amount).reduce(Money.zero(), Money::add);
        if (!sum.equals(orderTotal)) {
            throw new InvalidPayment("La suma de las formas de pago no cuadra con el total de la orden");
        }
        if (sum.isGreaterThan(BANKING_THRESHOLD)) {
            boolean cashWithoutFinancialSystem = appliedPayments.stream()
                    .anyMatch(applied -> applied.method() == SriPaymentMethod.CASH_NO_FINANCIAL_SYSTEM);
            if (cashWithoutFinancialSystem) {
                throw new InvalidPayment(
                        "Sobre USD 500 no se admite el código 01 de la Tabla 24, por bancarización");
            }
        }
        return new Payment(UUID.randomUUID(), chargeOrderId, shiftId, idempotencyKey, appliedPayments,
                chargedAt);
    }

    static Payment reconstruct(UUID id, UUID chargeOrderId, UUID shiftId, String idempotencyKey,
            List<AppliedPayment> appliedPayments, Instant chargedAt) {
        return new Payment(id, chargeOrderId, shiftId, idempotencyKey, appliedPayments, chargedAt);
    }

    public UUID id() {
        return id;
    }

    public UUID chargeOrderId() {
        return chargeOrderId;
    }

    public UUID shiftId() {
        return shiftId;
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }

    public List<AppliedPayment> appliedPayments() {
        return List.copyOf(appliedPayments);
    }

    public Instant chargedAt() {
        return chargedAt;
    }
}
