package com.mmqhali.cashier_service.domain.chargeorder;

import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shared.VatRate;

import java.util.Objects;
import java.util.UUID;

/**
 * Una prestación ya valorizada. Acá vive RF-06: una línea NOT_AUTHORIZED exige motivo y no
 * entra en ningún total; una línea CHARGEABLE no puede llevar motivo de rechazo. Es un estado
 * terminal, se fija en la valorización y no cambia — por eso es un record, no hace falta
 * distinguir fábrica de reconstrucción para algo que nunca muta.
 */
public record ChargeOrderLine(UUID id, String serviceCode, int quantity, Money agreementRate,
        Money recognizedAmount, Money copago, Money discount, VatRate vatRate, Status status,
        String rejectionReason) {

    public enum Status { CHARGEABLE, NOT_AUTHORIZED }

    public ChargeOrderLine {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(serviceCode, "serviceCode");
        Objects.requireNonNull(agreementRate, "agreementRate");
        Objects.requireNonNull(recognizedAmount, "recognizedAmount");
        Objects.requireNonNull(copago, "copago");
        Objects.requireNonNull(discount, "discount");
        Objects.requireNonNull(vatRate, "vatRate");
        Objects.requireNonNull(status, "status");
        if (quantity <= 0) {
            throw new InvalidChargeOrder("La cantidad debe ser mayor que cero");
        }
        if (status == Status.NOT_AUTHORIZED && (rejectionReason == null || rejectionReason.isBlank())) {
            throw new InvalidChargeOrder("Una línea no autorizada exige un motivo de rechazo");
        }
        if (status == Status.CHARGEABLE && rejectionReason != null) {
            throw new InvalidChargeOrder("Una línea cobrable no puede llevar motivo de rechazo");
        }
    }

    public static ChargeOrderLine chargeable(String serviceCode, int quantity, Money agreementRate,
            Money recognizedAmount, Money copago, Money discount, VatRate vatRate) {
        return new ChargeOrderLine(UUID.randomUUID(), serviceCode, quantity, agreementRate,
                recognizedAmount, copago, discount, vatRate, Status.CHARGEABLE, null);
    }

    public static ChargeOrderLine notAuthorized(String serviceCode, int quantity, Money agreementRate,
            Money recognizedAmount, Money copago, Money discount, VatRate vatRate, String rejectionReason) {
        return new ChargeOrderLine(UUID.randomUUID(), serviceCode, quantity, agreementRate,
                recognizedAmount, copago, discount, vatRate, Status.NOT_AUTHORIZED, rejectionReason);
    }

    public boolean isChargeable() {
        return status == Status.CHARGEABLE;
    }
}
