package com.mmqhali.cashier_service.application.payment;

import com.mmqhali.cashier_service.application.ResourceNotFoundException;
import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrder;
import com.mmqhali.cashier_service.domain.chargeorder.ChargeOrderRepository;
import com.mmqhali.cashier_service.domain.idempotency.IdempotencyRecord;
import com.mmqhali.cashier_service.domain.idempotency.IdempotencyRecordRepository;
import com.mmqhali.cashier_service.domain.outbox.OutboxEvent;
import com.mmqhali.cashier_service.domain.outbox.OutboxEventRepository;
import com.mmqhali.cashier_service.domain.payment.AppliedPayment;
import com.mmqhali.cashier_service.domain.payment.InvalidPayment;
import com.mmqhali.cashier_service.domain.payment.Payment;
import com.mmqhali.cashier_service.domain.payment.PaymentRepository;
import com.mmqhali.cashier_service.domain.shared.CardBrand;
import com.mmqhali.cashier_service.domain.shared.CardDetails;
import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shared.SriPaymentMethod;
import com.mmqhali.cashier_service.domain.shift.Shift;
import com.mmqhali.cashier_service.domain.shift.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RegistrarCobro. Todo pasa por una sola transacción: si la escritura del cobro, de la orden o
 * de la salida falla, no queda nada a medias. La restricción única de {@code idempotency_key}
 * en base de datos es la defensa real ante una carrera entre dos peticiones concurrentes con la
 * misma clave (regla no negociable 8); esta clase no reintenta esa carrera, la deja fallar como
 * fallo técnico — el cliente reintenta con la misma clave y en ese segundo intento ya la
 * encuentra en {@code idempotency_record}.
 */
@Service
public class RegisterPaymentUseCase {

    private static final Duration IDEMPOTENCY_TTL = Duration.ofDays(1);

    private final ChargeOrderRepository chargeOrderRepository;
    private final ShiftRepository shiftRepository;
    private final PaymentRepository paymentRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final JsonMapper jsonMapper;
    private final Clock clock;

    public RegisterPaymentUseCase(ChargeOrderRepository chargeOrderRepository, ShiftRepository shiftRepository,
            PaymentRepository paymentRepository, IdempotencyRecordRepository idempotencyRecordRepository,
            OutboxEventRepository outboxEventRepository, JsonMapper jsonMapper, Clock clock) {
        this.chargeOrderRepository = chargeOrderRepository;
        this.shiftRepository = shiftRepository;
        this.paymentRepository = paymentRepository;
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.jsonMapper = jsonMapper;
        this.clock = clock;
    }

    @Transactional
    public PaymentResult execute(RegisterPaymentCommand command) {
        String requestHash = hash(command);
        var existing = idempotencyRecordRepository.findByKey(command.idempotencyKey());
        if (existing.isPresent()) {
            if (!existing.get().matchesRequest(requestHash)) {
                throw new IdempotencyConflictException(command.idempotencyKey());
            }
            return deserialize(existing.get().result());
        }

        ChargeOrder chargeOrder = chargeOrderRepository.findById(command.chargeOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Orden de cobro no encontrada: " + command.chargeOrderId()));
        Shift shift = shiftRepository.findById(command.shiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado: " + command.shiftId()));

        Instant now = clock.instant();
        List<AppliedPayment> appliedPayments = command.appliedPayments().stream()
                .map(this::toAppliedPayment)
                .collect(Collectors.toList());

        Payment payment = Payment.register(chargeOrder.id(), chargeOrder.status(), chargeOrder.totalToCharge(),
                shift.id(), shift.status(), command.idempotencyKey(), appliedPayments, now);
        paymentRepository.save(payment);

        chargeOrder.markCharged();
        chargeOrderRepository.save(chargeOrder);

        PaymentResult result = PaymentResult.from(payment);
        String serializedResult = serialize(result);
        outboxEventRepository.save(
                OutboxEvent.create("Payment", payment.id(), "PaymentRegistered", serializedResult, now));
        idempotencyRecordRepository.save(IdempotencyRecord.create(command.idempotencyKey(), requestHash,
                serializedResult, now, now.plus(IDEMPOTENCY_TTL)));

        return result;
    }

    private AppliedPayment toAppliedPayment(AppliedPaymentInput input) {
        SriPaymentMethod method = SriPaymentMethod.fromSriCode(input.sriCode());
        CardDetails cardDetails = null;
        if (method.isCard()) {
            cardDetails = new CardDetails(input.cardBank(), parseCardBrand(input.cardBrand()));
        }
        return AppliedPayment.of(method, Money.of(input.amount()), input.reference(), cardDetails,
                input.termDays(), input.timeUnit());
    }

    private CardBrand parseCardBrand(String cardBrand) {
        try {
            return cardBrand == null ? null : CardBrand.valueOf(cardBrand);
        } catch (IllegalArgumentException e) {
            throw new InvalidPayment("Marca de tarjeta desconocida: " + cardBrand);
        }
    }

    private String hash(RegisterPaymentCommand command) {
        byte[] bytes = jsonMapper.writeValueAsBytes(command);
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo calcular la huella de la petición", e);
        }
    }

    private String serialize(PaymentResult result) {
        return jsonMapper.writeValueAsString(result);
    }

    private PaymentResult deserialize(String json) {
        return jsonMapper.readValue(json, PaymentResult.class);
    }
}
