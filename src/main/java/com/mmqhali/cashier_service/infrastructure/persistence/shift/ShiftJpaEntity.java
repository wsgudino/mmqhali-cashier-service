package com.mmqhali.cashier_service.infrastructure.persistence.shift;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Espejo de la tabla shift. Sin reglas de negocio, esas viven en el agregado (D18). */
@Entity
@Table(name = "shift")
public class ShiftJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String cashier;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private String establishment;

    @Column(name = "issuing_point", nullable = false)
    private String issuingPoint;

    @Column(name = "opening_float", nullable = false)
    private BigDecimal openingFloat;

    @Column(name = "cash_counted")
    private BigDecimal cashCounted;

    private BigDecimal difference;

    @Column(nullable = false)
    private String status;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PaymentMethodTotalJpaEntity> methodTotals = new ArrayList<>();

    public ShiftJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }

    public String getIssuingPoint() {
        return issuingPoint;
    }

    public void setIssuingPoint(String issuingPoint) {
        this.issuingPoint = issuingPoint;
    }

    public BigDecimal getOpeningFloat() {
        return openingFloat;
    }

    public void setOpeningFloat(BigDecimal openingFloat) {
        this.openingFloat = openingFloat;
    }

    public BigDecimal getCashCounted() {
        return cashCounted;
    }

    public void setCashCounted(BigDecimal cashCounted) {
        this.cashCounted = cashCounted;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Instant openedAt) {
        this.openedAt = openedAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public List<PaymentMethodTotalJpaEntity> getMethodTotals() {
        return methodTotals;
    }

    public void setMethodTotals(List<PaymentMethodTotalJpaEntity> methodTotals) {
        this.methodTotals.clear();
        this.methodTotals.addAll(methodTotals);
    }
}
