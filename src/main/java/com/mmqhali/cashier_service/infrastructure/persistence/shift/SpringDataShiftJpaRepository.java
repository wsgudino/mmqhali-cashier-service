package com.mmqhali.cashier_service.infrastructure.persistence.shift;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataShiftJpaRepository extends JpaRepository<ShiftJpaEntity, UUID> {

    Optional<ShiftJpaEntity> findByCashierAndBranchAndStatus(String cashier, String branch, String status);
}
