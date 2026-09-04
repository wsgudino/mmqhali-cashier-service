package com.mmqhali.cashier_service.domain.shift;

import com.mmqhali.cashier_service.domain.shared.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * El día de trabajo de un cajero. No contiene la colección de cobros: un turno con mil cobros
 * no se carga entero para cerrarse, los cobros referencian al turno.
 *
 * <p>Dos caminos de entrada: {@link #open} es la fábrica pública que valida; {@link #reconstruct}
 * es de paquete, para que el mapeador de persistencia arme el turno ya guardado sin revalidar
 * (cuando se lee un turno de ayer, no tiene sentido volver a correr "no se abre con fondo
 * negativo").
 */
public final class Shift {

    public enum Status { OPEN, CLOSED }

    private final UUID id;
    private final String cashier;
    private final String branch;
    private final String establishment;
    private final String issuingPoint;
    private final Money openingFloat;
    private final Instant openedAt;
    private final List<PaymentMethodTotal> methodTotals;
    private Money cashCounted;
    private Money difference;
    private Status status;
    private Instant closedAt;

    private Shift(UUID id, String cashier, String branch, String establishment, String issuingPoint,
            Money openingFloat, Instant openedAt, List<PaymentMethodTotal> methodTotals, Money cashCounted,
            Money difference, Status status, Instant closedAt) {
        this.id = id;
        this.cashier = cashier;
        this.branch = branch;
        this.establishment = establishment;
        this.issuingPoint = issuingPoint;
        this.openingFloat = openingFloat;
        this.openedAt = openedAt;
        this.methodTotals = new ArrayList<>(methodTotals);
        this.cashCounted = cashCounted;
        this.difference = difference;
        this.status = status;
        this.closedAt = closedAt;
    }

    public static Shift open(String cashier, String branch, String establishment, String issuingPoint,
            Money openingFloat, Instant openedAt) {
        Objects.requireNonNull(cashier, "cashier");
        Objects.requireNonNull(branch, "branch");
        Objects.requireNonNull(establishment, "establishment");
        Objects.requireNonNull(issuingPoint, "issuingPoint");
        Objects.requireNonNull(openedAt, "openedAt");
        if (openingFloat == null || openingFloat.isNegative()) {
            throw new InvalidShift("El fondo inicial no puede ser negativo");
        }
        return new Shift(UUID.randomUUID(), cashier, branch, establishment, issuingPoint, openingFloat,
                openedAt, List.of(), null, null, Status.OPEN, null);
    }

    static Shift reconstruct(UUID id, String cashier, String branch, String establishment,
            String issuingPoint, Money openingFloat, Instant openedAt, List<PaymentMethodTotal> methodTotals,
            Money cashCounted, Money difference, Status status, Instant closedAt) {
        return new Shift(id, cashier, branch, establishment, issuingPoint, openingFloat, openedAt,
                methodTotals, cashCounted, difference, status, closedAt);
    }

    /**
     * La diferencia solo considera efectivo: sale de comparar lo contado contra el fondo inicial
     * más lo cobrado en efectivo. Tarjetas y transferencias no entran, ese dinero nunca estuvo
     * en el cajón.
     */
    public void close(Money cashCounted, Money cashCollectedInCash, List<PaymentMethodTotal> methodTotals,
            Instant closedAt) {
        if (status == Status.CLOSED) {
            throw new InvalidShift("El turno ya está cerrado");
        }
        Objects.requireNonNull(cashCounted, "cashCounted");
        Objects.requireNonNull(cashCollectedInCash, "cashCollectedInCash");
        Objects.requireNonNull(methodTotals, "methodTotals");
        Objects.requireNonNull(closedAt, "closedAt");
        this.methodTotals.clear();
        this.methodTotals.addAll(methodTotals);
        this.cashCounted = cashCounted;
        this.difference = cashCounted.subtract(openingFloat.add(cashCollectedInCash));
        this.status = Status.CLOSED;
        this.closedAt = closedAt;
    }

    public boolean isOpen() {
        return status == Status.OPEN;
    }

    public UUID id() {
        return id;
    }

    public String cashier() {
        return cashier;
    }

    public String branch() {
        return branch;
    }

    public String establishment() {
        return establishment;
    }

    public String issuingPoint() {
        return issuingPoint;
    }

    public Money openingFloat() {
        return openingFloat;
    }

    public Instant openedAt() {
        return openedAt;
    }

    public List<PaymentMethodTotal> methodTotals() {
        return List.copyOf(methodTotals);
    }

    public Money cashCounted() {
        return cashCounted;
    }

    public Money difference() {
        return difference;
    }

    public Status status() {
        return status;
    }

    public Instant closedAt() {
        return closedAt;
    }
}
