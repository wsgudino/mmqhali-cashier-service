package com.mmqhali.cashier_service.application.chargeorder;

import java.time.Instant;
import java.util.List;

/**
 * CargarOrdenDeCobro con monto a mano. El tercero a facturar viaja embebido: si ya existe uno
 * con esa identificación se reutiliza (D14 / invoice_recipient), si no se registra.
 */
public record LoadChargeOrderCommand(String careId, String patientIdType, String patientIdNumber,
        String patientName, String recipientIdType, String recipientIdNumber, String recipientName,
        String recipientAddress, String recipientEmail, String recipientPhone, String agreementRef,
        Instant careDate, List<ChargeOrderLineInput> lines) {
}
