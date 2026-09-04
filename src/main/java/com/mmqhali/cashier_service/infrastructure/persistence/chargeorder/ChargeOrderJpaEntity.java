package com.mmqhali.cashier_service.infrastructure.persistence.chargeorder;

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

/** Espejo de la tabla charge_order. Sin reglas de negocio, esas viven en el agregado (D18). */
@Entity
@Table(name = "charge_order")
public class ChargeOrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "care_id", nullable = false, unique = true)
    private String careId;

    @Column(name = "patient_id_type", nullable = false)
    private String patientIdType;

    @Column(name = "patient_id_number", nullable = false)
    private String patientIdNumber;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "invoice_recipient_id", nullable = false)
    private UUID invoiceRecipientId;

    @Column(name = "agreement_ref")
    private String agreementRef;

    @Column(nullable = false)
    private String status;

    @Column(name = "care_date", nullable = false)
    private Instant careDate;

    @OneToMany(mappedBy = "chargeOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ChargeOrderLineJpaEntity> lines = new ArrayList<>();

    public ChargeOrderJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCareId() {
        return careId;
    }

    public void setCareId(String careId) {
        this.careId = careId;
    }

    public String getPatientIdType() {
        return patientIdType;
    }

    public void setPatientIdType(String patientIdType) {
        this.patientIdType = patientIdType;
    }

    public String getPatientIdNumber() {
        return patientIdNumber;
    }

    public void setPatientIdNumber(String patientIdNumber) {
        this.patientIdNumber = patientIdNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public UUID getInvoiceRecipientId() {
        return invoiceRecipientId;
    }

    public void setInvoiceRecipientId(UUID invoiceRecipientId) {
        this.invoiceRecipientId = invoiceRecipientId;
    }

    public String getAgreementRef() {
        return agreementRef;
    }

    public void setAgreementRef(String agreementRef) {
        this.agreementRef = agreementRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCareDate() {
        return careDate;
    }

    public void setCareDate(Instant careDate) {
        this.careDate = careDate;
    }

    public List<ChargeOrderLineJpaEntity> getLines() {
        return lines;
    }

    public void setLines(List<ChargeOrderLineJpaEntity> lines) {
        this.lines.clear();
        this.lines.addAll(lines);
    }
}
