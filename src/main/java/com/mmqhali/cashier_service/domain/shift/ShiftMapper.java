package com.mmqhali.cashier_service.domain.shift;

import com.mmqhali.cashier_service.domain.shared.SriPaymentMethod;
import com.mmqhali.cashier_service.infrastructure.persistence.shared.MoneyConverter;
import com.mmqhali.cashier_service.infrastructure.persistence.shift.PaymentMethodTotalJpaEntity;
import com.mmqhali.cashier_service.infrastructure.persistence.shift.ShiftJpaEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Traduce entre {@link Shift} y su entidad JPA (D19, mapeo escrito a mano). Vive en el paquete
 * del agregado porque {@link Shift#reconstruct} es de paquete: solo así puede reconstruir un
 * turno guardado sin pasar por las validaciones de {@link Shift#open}.
 */
public final class ShiftMapper {

    private ShiftMapper() {
    }

    public static ShiftJpaEntity toEntity(Shift shift) {
        ShiftJpaEntity entity = new ShiftJpaEntity();
        entity.setId(shift.id());
        entity.setCashier(shift.cashier());
        entity.setBranch(shift.branch());
        entity.setEstablishment(shift.establishment());
        entity.setIssuingPoint(shift.issuingPoint());
        entity.setOpeningFloat(MoneyConverter.toColumn(shift.openingFloat()));
        entity.setCashCounted(MoneyConverter.toColumn(shift.cashCounted()));
        entity.setDifference(MoneyConverter.toColumn(shift.difference()));
        entity.setStatus(shift.status().name());
        entity.setOpenedAt(shift.openedAt());
        entity.setClosedAt(shift.closedAt());
        entity.setMethodTotals(toMethodTotalEntities(shift.methodTotals(), entity));
        return entity;
    }

    public static Shift toDomain(ShiftJpaEntity entity) {
        List<PaymentMethodTotal> methodTotals = entity.getMethodTotals().stream()
                .map(ShiftMapper::toMethodTotalDomain)
                .collect(Collectors.toList());
        return Shift.reconstruct(entity.getId(), entity.getCashier(), entity.getBranch(),
                entity.getEstablishment(), entity.getIssuingPoint(), MoneyConverter.toDomain(entity.getOpeningFloat()),
                entity.getOpenedAt(), methodTotals, MoneyConverter.toDomain(entity.getCashCounted()),
                MoneyConverter.toDomain(entity.getDifference()), Shift.Status.valueOf(entity.getStatus()),
                entity.getClosedAt());
    }

    private static List<PaymentMethodTotalJpaEntity> toMethodTotalEntities(List<PaymentMethodTotal> totals,
            ShiftJpaEntity owner) {
        return totals.stream().map(total -> {
            PaymentMethodTotalJpaEntity entity = new PaymentMethodTotalJpaEntity();
            entity.setId(total.id());
            entity.setShift(owner);
            entity.setSriCode(total.method().sriCode());
            entity.setTotal(MoneyConverter.toColumn(total.total()));
            return entity;
        }).collect(Collectors.toList());
    }

    private static PaymentMethodTotal toMethodTotalDomain(PaymentMethodTotalJpaEntity entity) {
        return new PaymentMethodTotal(entity.getId(), SriPaymentMethod.fromSriCode(entity.getSriCode()),
                MoneyConverter.toDomain(entity.getTotal()));
    }
}
