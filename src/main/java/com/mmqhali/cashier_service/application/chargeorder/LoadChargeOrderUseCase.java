package com.mmqhali.cashier_service.application.chargeorder;

import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrder;
import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrderLine;
import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrderRepository;
import com.mmqhali.cashier_service.domain.chargeorder.InvalidChargeOrder;
import com.mmqhali.cashier_service.domain.invoicerecipient.InvoiceRecipient;
import com.mmqhali.cashier_service.domain.invoicerecipient.InvoiceRecipientRepository;
import com.mmqhali.cashier_service.domain.shared.Identification;
import com.mmqhali.cashier_service.domain.shared.IdentificationType;
import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shared.VatRate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CargarOrdenDeCobro con monto a mano: sin valorización real, las líneas ya vienen priceadas
 * por el cajero. Si la misma atención ya se cargó, se devuelve la orden existente tal cual
 * quedó — no es la idempotencia con huella de RegistrarCobro, es la regla del catálogo
 * (care_id es único).
 */
@Service
public class LoadChargeOrderUseCase {

    private final ChargeOrderRepository chargeOrderRepository;
    private final InvoiceRecipientRepository invoiceRecipientRepository;
    private final Clock clock;

    public LoadChargeOrderUseCase(ChargeOrderRepository chargeOrderRepository,
            InvoiceRecipientRepository invoiceRecipientRepository, Clock clock) {
        this.chargeOrderRepository = chargeOrderRepository;
        this.invoiceRecipientRepository = invoiceRecipientRepository;
        this.clock = clock;
    }

    @Transactional
    public ChargeOrderResult execute(LoadChargeOrderCommand command) {
        var existing = chargeOrderRepository.findByCareId(command.careId());
        if (existing.isPresent()) {
            return ChargeOrderResult.from(existing.get());
        }

        Identification recipientIdentification = new Identification(
                IdentificationType.fromSriCode(command.recipientIdType()), command.recipientIdNumber());
        InvoiceRecipient recipient = invoiceRecipientRepository.findByIdentification(recipientIdentification)
                .orElseGet(() -> invoiceRecipientRepository.save(InvoiceRecipient.register(recipientIdentification,
                        command.recipientName(), command.recipientAddress(), command.recipientEmail(),
                        command.recipientPhone(), clock.instant())));

        Identification patientIdentification = new Identification(
                IdentificationType.fromSriCode(command.patientIdType()), command.patientIdNumber());
        Instant careDate = command.careDate() != null ? command.careDate() : clock.instant();

        ChargeOrder chargeOrder = ChargeOrder.createPending(command.careId(), patientIdentification,
                command.patientName(), recipient.id(), careDate);

        List<ChargeOrderLine> lines = command.lines().stream().map(this::toLine).collect(Collectors.toList());
        chargeOrder.applyPricing(command.agreementRef(), lines);

        chargeOrderRepository.save(chargeOrder);
        return ChargeOrderResult.from(chargeOrder);
    }

    private ChargeOrderLine toLine(ChargeOrderLineInput input) {
        VatRate vatRate = VatRate.fromSriPercentageCode(input.vatRate());
        Money agreementRate = Money.of(input.agreementRate());
        Money recognizedAmount = Money.of(input.recognizedAmount());
        Money copago = Money.of(input.copago());
        Money discount = Money.of(input.discount());
        return switch (input.status()) {
            case "CHARGEABLE" -> ChargeOrderLine.chargeable(input.serviceCode(), input.quantity(), agreementRate,
                    recognizedAmount, copago, discount, vatRate);
            case "NOT_AUTHORIZED" -> ChargeOrderLine.notAuthorized(input.serviceCode(), input.quantity(),
                    agreementRate, recognizedAmount, copago, discount, vatRate, input.rejectionReason());
            default -> throw new InvalidChargeOrder("Estado de línea desconocido: " + input.status());
        };
    }
}
