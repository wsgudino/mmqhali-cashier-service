package com.mmqhali.cashier_service.infrastructure.persistence.chargeorder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/** Espejo de charge_order_line. Acá vive RF-06 en el dominio, no acá. */
@Entity
@Table(name = "charge_order_line")
public class ChargeOrderLineJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_order_id", nullable = false)
    private ChargeOrderJpaEntity chargeOrder;

    @Column(name = "service_code", nullable = false)
    private String serviceCode;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "agreement_rate", nullable = false)
    private BigDecimal agreementRate;

    @Column(name = "recognized_amount", nullable = false)
    private BigDecimal recognizedAmount;

    @Column(nullable = false)
    private BigDecimal copago;

    @Column(nullable = false)
    private BigDecimal discount;

    @Column(name = "vat_rate", nullable = false)
    private String vatRate;

    @Column(nullable = false)
    private String status;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    public ChargeOrderLineJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ChargeOrderJpaEntity getChargeOrder() {
        return chargeOrder;
    }

    public void setChargeOrder(ChargeOrderJpaEntity chargeOrder) {
        this.chargeOrder = chargeOrder;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAgreementRate() {
        return agreementRate;
    }

    public void setAgreementRate(BigDecimal agreementRate) {
        this.agreementRate = agreementRate;
    }

    public BigDecimal getRecognizedAmount() {
        return recognizedAmount;
    }

    public void setRecognizedAmount(BigDecimal recognizedAmount) {
        this.recognizedAmount = recognizedAmount;
    }

    public BigDecimal getCopago() {
        return copago;
    }

    public void setCopago(BigDecimal copago) {
        this.copago = copago;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getVatRate() {
        return vatRate;
    }

    public void setVatRate(String vatRate) {
        this.vatRate = vatRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
