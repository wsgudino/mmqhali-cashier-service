package com.mmqhali.cashier_service.infrastructure.web.payment;

import com.mmqhali.cashier_service.application.payment.PaymentResult;
import com.mmqhali.cashier_service.application.payment.RegisterPaymentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/shifts/{shiftId}/charge-orders/{chargeOrderId}/payments")
public class PaymentController {

    private final RegisterPaymentUseCase registerPaymentUseCase;

    public PaymentController(RegisterPaymentUseCase registerPaymentUseCase) {
        this.registerPaymentUseCase = registerPaymentUseCase;
    }

    @PostMapping
    public ResponseEntity<PaymentResult> register(@PathVariable UUID shiftId, @PathVariable UUID chargeOrderId,
            @RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody RegisterPaymentRequest request) {
        PaymentResult result = registerPaymentUseCase.execute(request.toCommand(chargeOrderId, shiftId, idempotencyKey));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
