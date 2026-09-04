package com.mmqhali.cashier_service.domain.chargeorder;

import com.mmqhali.cashier_service.domain.shared.Identification;
import com.mmqhali.cashier_service.domain.shared.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Qué hay que cobrarle al paciente. Los totales no se guardan, se derivan de las líneas
 * cobrables — un total guardado se desincroniza de su detalle tarde o temprano.
 *
 * <p>{@link #createPending} es la fábrica pública; {@link #reconstruct}, de paquete, para el
 * mapeador. La carga manual de la orden (hoy, monto a mano) es un camino hacia este mismo
 * agregado, no un flujo aparte: cuando entre la recepción desde el HIS, tiene que poder
 * enchufarse como segundo camino sin reestructurar esta clase.
 */
public final class ChargeOrder {

    public enum Status { PENDING_PRICING, PRICED, CHARGED, CANCELLED }

    private final UUID id;
    private final String careId;
    private final Identification patientIdentification;
    private final String patientName;
    private final UUID invoiceRecipientId;
    private final Instant careDate;
    private final List<ChargeOrderLine> lines;
    private String agreementRef;
    private Status status;

    private ChargeOrder(UUID id, String careId, Identification patientIdentification, String patientName,
            UUID invoiceRecipientId, String agreementRef, Status status, Instant careDate,
            List<ChargeOrderLine> lines) {
        this.id = id;
        this.careId = careId;
        this.patientIdentification = patientIdentification;
        this.patientName = patientName;
        this.invoiceRecipientId = invoiceRecipientId;
        this.agreementRef = agreementRef;
        this.status = status;
        this.careDate = careDate;
        this.lines = new ArrayList<>(lines);
    }

    public static ChargeOrder createPending(String careId, Identification patientIdentification,
            String patientName, UUID invoiceRecipientId, Instant careDate) {
        Objects.requireNonNull(careId, "careId");
        Objects.requireNonNull(patientIdentification, "patientIdentification");
        Objects.requireNonNull(invoiceRecipientId, "invoiceRecipientId");
        Objects.requireNonNull(careDate, "careDate");
        return new ChargeOrder(UUID.randomUUID(), careId, patientIdentification, patientName,
                invoiceRecipientId, null, Status.PENDING_PRICING, careDate, List.of());
    }

    static ChargeOrder reconstruct(UUID id, String careId, Identification patientIdentification,
            String patientName, UUID invoiceRecipientId, String agreementRef, Status status,
            Instant careDate, List<ChargeOrderLine> lines) {
        return new ChargeOrder(id, careId, patientIdentification, patientName, invoiceRecipientId,
                agreementRef, status, careDate, lines);
    }

    public void applyPricing(String agreementRef, List<ChargeOrderLine> pricedLines) {
        if (status != Status.PENDING_PRICING) {
            throw new InvalidChargeOrder("La orden ya fue valorizada");
        }
        if (pricedLines == null || pricedLines.isEmpty()) {
            throw new InvalidChargeOrder("Una orden sin líneas no es valorizable");
        }
        this.agreementRef = agreementRef;
        this.lines.clear();
        this.lines.addAll(pricedLines);
        this.status = Status.PRICED;
    }

    public void markCharged() {
        if (status != Status.PRICED) {
            throw new InvalidChargeOrder("Solo se cobra una orden en estado PRICED");
        }
        this.status = Status.CHARGED;
    }

    /** Lo que efectivamente se le cobra al paciente: la suma del copago de las líneas cobrables. */
    public Money totalToCharge() {
        return chargeableLines().map(ChargeOrderLine::copago).reduce(Money.zero(), Money::add);
    }

    /**
     * El total de acreencia contra la aseguradora. Se calcula aunque la liquidación esté fuera
     * de alcance: si no se captura acá, después no hay forma de reconstruirlo.
     */
    public Money totalReceivable() {
        return chargeableLines().map(ChargeOrderLine::recognizedAmount).reduce(Money.zero(), Money::add);
    }

    private Stream<ChargeOrderLine> chargeableLines() {
        return lines.stream().filter(ChargeOrderLine::isChargeable);
    }

    public UUID id() {
        return id;
    }

    public String careId() {
        return careId;
    }

    public Identification patientIdentification() {
        return patientIdentification;
    }

    public String patientName() {
        return patientName;
    }

    public UUID invoiceRecipientId() {
        return invoiceRecipientId;
    }

    public String agreementRef() {
        return agreementRef;
    }

    public Status status() {
        return status;
    }

    public Instant careDate() {
        return careDate;
    }

    public List<ChargeOrderLine> lines() {
        return List.copyOf(lines);
    }
}
