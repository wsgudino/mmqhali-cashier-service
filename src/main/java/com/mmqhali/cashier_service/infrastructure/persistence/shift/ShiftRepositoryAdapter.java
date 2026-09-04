package com.mmqhali.cashier_service.infrastructure.persistence.shift;

import com.mmqhali.cashier_service.domain.shift.Shift;
import com.mmqhali.cashier_service.domain.shift.ShiftMapper;
import com.mmqhali.cashier_service.domain.shift.ShiftRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class ShiftRepositoryAdapter implements ShiftRepository {

    private final SpringDataShiftJpaRepository jpaRepository;

    ShiftRepositoryAdapter(SpringDataShiftJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Shift save(Shift shift) {
        return ShiftMapper.toDomain(jpaRepository.save(ShiftMapper.toEntity(shift)));
    }

    @Override
    public Optional<Shift> findById(UUID id) {
        return jpaRepository.findById(id).map(ShiftMapper::toDomain);
    }

    @Override
    public Optional<Shift> findOpenShift(String cashier, String branch) {
        return jpaRepository.findByCashierAndBranchAndStatus(cashier, branch, Shift.Status.OPEN.name())
                .map(ShiftMapper::toDomain);
    }
}
