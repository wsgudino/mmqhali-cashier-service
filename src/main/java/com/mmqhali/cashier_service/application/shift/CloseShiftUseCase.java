package com.mmqhali.cashier_service.application.shift;

import com.mmqhali.cashier_service.application.ResourceNotFoundException;
import com.mmqhali.cashier_service.domain.audit.AuditEvent;
import com.mmqhali.cashier_service.domain.audit.AuditEventRepository;
import com.mmqhali.cashier_service.domain.payment.Payment;
import com.mmqhali.cashier_service.domain.payment.PaymentRepository;
import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shared.SriPaymentMethod;
import com.mmqhali.cashier_service.domain.shift.PaymentMethodTotal;
import com.mmqhali.cashier_service.domain.shift.Shift;
import com.mmqhali.cashier_service.domain.shift.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CerrarTurno con arqueo. Los totales por forma de pago son una foto compuesta de los cobros
 * del turno al cerrar (regla 1.6: ningún total se guarda si se puede derivar) — se recorren una
 * sola vez acá, nunca se van acumulando cobro a cobro.
 */
@Service
public class CloseShiftUseCase {

    private final ShiftRepository shiftRepository;
    private final PaymentRepository paymentRepository;
    private final AuditEventRepository auditEventRepository;
    private final JsonMapper jsonMapper;
    private final Clock clock;

    public CloseShiftUseCase(ShiftRepository shiftRepository, PaymentRepository paymentRepository,
            AuditEventRepository auditEventRepository, JsonMapper jsonMapper, Clock clock) {
        this.shiftRepository = shiftRepository;
        this.paymentRepository = paymentRepository;
        this.auditEventRepository = auditEventRepository;
        this.jsonMapper = jsonMapper;
        this.clock = clock;
    }

    @Transactional
    public ShiftResult execute(CloseShiftCommand command) {
        Shift shift = shiftRepository.findById(command.shiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado: " + command.shiftId()));

        List<Payment> payments = paymentRepository.findByShiftId(shift.id());
        Map<SriPaymentMethod, Money> totalsByMethod = new EnumMap<>(SriPaymentMethod.class);
        payments.stream()
                .flatMap(payment -> payment.appliedPayments().stream())
                .forEach(applied -> totalsByMethod.merge(applied.method(), applied.amount(), Money::add));

        List<PaymentMethodTotal> methodTotals = totalsByMethod.entrySet().stream()
                .map(entry -> PaymentMethodTotal.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        Money cashCollectedInCash = totalsByMethod.getOrDefault(SriPaymentMethod.CASH_NO_FINANCIAL_SYSTEM,
                Money.zero());

        Instant now = clock.instant();
        shift.close(Money.of(command.cashCounted()), cashCollectedInCash, methodTotals, now);
        shiftRepository.save(shift);

        ShiftResult result = ShiftResult.from(shift);
        auditEventRepository.save(AuditEvent.create(shift.cashier(), "ShiftClosed", "Shift", shift.id(),
                jsonMapper.writeValueAsString(result), now));

        return result;
    }
}
