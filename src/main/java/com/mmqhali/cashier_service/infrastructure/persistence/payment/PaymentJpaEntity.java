package com.mmqhali.cashier_service.infrastructure.persistence.payment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Espejo de la tabla payment. Sin reglas de negocio, esas viven en el agregado (D18). */
@Entity
@Table(name = "payment")
public class PaymentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "charge_order_id", nullable = false, unique = true)
    private UUID chargeOrderId;

    @Column(name = "shift_id", nullable = false)
    private UUID shiftId;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "charged_at", nullable = false)
    private Instant chargedAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AppliedPaymentJpaEntity> appliedPayments = new ArrayList<>();

    public PaymentJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getChargeOrderId() {
        return chargeOrderId;
    }

    public void setChargeOrderId(UUID chargeOrderId) {
        this.chargeOrderId = chargeOrderId;
    }

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getChargedAt() {
        return chargedAt;
    }

    public void setChargedAt(Instant chargedAt) {
        this.chargedAt = chargedAt;
    }

    public List<AppliedPaymentJpaEntity> getAppliedPayments() {
        return appliedPayments;
    }

    public void setAppliedPayments(List<AppliedPaymentJpaEntity> appliedPayments) {
        this.appliedPayments.clear();
        this.appliedPayments.addAll(appliedPayments);
    }
}
