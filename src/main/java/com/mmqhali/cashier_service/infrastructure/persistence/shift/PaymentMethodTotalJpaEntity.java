package com.mmqhali.cashier_service.infrastructure.persistence.shift;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/** Espejo de payment_method_total: foto del turno por forma de pago, tomada al cerrar. */
@Entity
@Table(name = "payment_method_total")
public class PaymentMethodTotalJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftJpaEntity shift;

    @Column(name = "sri_code", nullable = false)
    private String sriCode;

    @Column(nullable = false)
    private BigDecimal total;

    public PaymentMethodTotalJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ShiftJpaEntity getShift() {
        return shift;
    }

    public void setShift(ShiftJpaEntity shift) {
        this.shift = shift;
    }

    public String getSriCode() {
        return sriCode;
    }

    public void setSriCode(String sriCode) {
        this.sriCode = sriCode;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
