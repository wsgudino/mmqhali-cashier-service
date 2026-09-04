package com.mmqhali.cashier_service.infrastructure.persistence.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/** Espejo de applied_payment. Cada forma de pago usada en un cobro. */
@Entity
@Table(name = "applied_payment")
public class AppliedPaymentJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentJpaEntity payment;

    @Column(name = "sri_code", nullable = false)
    private String sriCode;

    @Column(nullable = false)
    private BigDecimal amount;

    private String reference;

    @Column(name = "card_bank")
    private String cardBank;

    @Column(name = "card_brand")
    private String cardBrand;

    @Column(name = "term_days", nullable = false)
    private int termDays;

    @Column(name = "time_unit", nullable = false)
    private String timeUnit;

    public AppliedPaymentJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PaymentJpaEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentJpaEntity payment) {
        this.payment = payment;
    }

    public String getSriCode() {
        return sriCode;
    }

    public void setSriCode(String sriCode) {
        this.sriCode = sriCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCardBank() {
        return cardBank;
    }

    public void setCardBank(String cardBank) {
        this.cardBank = cardBank;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public int getTermDays() {
        return termDays;
    }

    public void setTermDays(int termDays) {
        this.termDays = termDays;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}
