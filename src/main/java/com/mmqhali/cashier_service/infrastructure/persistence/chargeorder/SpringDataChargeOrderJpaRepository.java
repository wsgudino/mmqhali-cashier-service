package com.mmqhali.cashier_service.infrastructure.persistence.chargeorder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataChargeOrderJpaRepository extends JpaRepository<ChargeOrderJpaEntity, UUID> {

    Optional<ChargeOrderJpaEntity> findByCareId(String careId);
}
