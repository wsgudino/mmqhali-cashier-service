package com.mmqhali.cashier_service.domain.shift;

import java.util.Optional;
import java.util.UUID;

/** Puerto de persistencia del turno. Sin dependencia de Spring ni JPA (regla 9 de CLAUDE.md). */
public interface ShiftRepository {

    Shift save(Shift shift);

    Optional<Shift> findById(UUID id);

    /** No puede haber dos turnos abiertos para el mismo cajero y sede. */
    Optional<Shift> findOpenShift(String cashier, String branch);
}
