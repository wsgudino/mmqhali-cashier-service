package com.mmqhali.cashier_service.application.shift;

import com.mmqhali.cashier_service.domain.shared.Money;
import com.mmqhali.cashier_service.domain.shift.InvalidShift;
import com.mmqhali.cashier_service.domain.shift.Shift;
import com.mmqhali.cashier_service.domain.shift.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/** AbrirTurno. No puede haber dos turnos abiertos para el mismo cajero y sede. */
@Service
public class OpenShiftUseCase {

    private final ShiftRepository shiftRepository;
    private final Clock clock;

    public OpenShiftUseCase(ShiftRepository shiftRepository, Clock clock) {
        this.shiftRepository = shiftRepository;
        this.clock = clock;
    }

    @Transactional
    public ShiftResult execute(OpenShiftCommand command) {
        if (command.cashier() == null || command.cashier().isBlank()) {
            throw new InvalidShift("El cajero es obligatorio");
        }
        if (shiftRepository.findOpenShift(command.cashier(), command.branch()).isPresent()) {
            throw new InvalidShift("Ya hay un turno abierto para este cajero y sede");
        }
        Shift shift = Shift.open(command.cashier(), command.branch(), command.establishment(),
                command.issuingPoint(), Money.of(command.openingFloat()), clock.instant());
        shiftRepository.save(shift);
        return ShiftResult.from(shift);
    }
}
