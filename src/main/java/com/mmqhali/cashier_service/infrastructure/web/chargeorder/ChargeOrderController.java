package com.mmqhali.cashier_service.infrastructure.web.chargeorder;

import com.mmqhali.cashier_service.application.chargeorder.ChargeOrderResult;
import com.mmqhali.cashier_service.application.chargeorder.LoadChargeOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/charge-orders")
public class ChargeOrderController {

    private final LoadChargeOrderUseCase loadChargeOrderUseCase;

    public ChargeOrderController(LoadChargeOrderUseCase loadChargeOrderUseCase) {
        this.loadChargeOrderUseCase = loadChargeOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<ChargeOrderResult> load(@Valid @RequestBody LoadChargeOrderRequest request) {
        ChargeOrderResult result = loadChargeOrderUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
