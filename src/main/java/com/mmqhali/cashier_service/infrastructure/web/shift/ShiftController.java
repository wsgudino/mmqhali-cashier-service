package com.mmqhali.cashier_service.infrastructure.web.shift;

import com.mmqhali.cashier_service.application.shift.CloseShiftUseCase;
import com.mmqhali.cashier_service.application.shift.OpenShiftUseCase;
import com.mmqhali.cashier_service.application.shift.ShiftResult;
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
@RequestMapping("/api/shifts")
public class ShiftController {

    private final OpenShiftUseCase openShiftUseCase;
    private final CloseShiftUseCase closeShiftUseCase;

    public ShiftController(OpenShiftUseCase openShiftUseCase, CloseShiftUseCase closeShiftUseCase) {
        this.openShiftUseCase = openShiftUseCase;
        this.closeShiftUseCase = closeShiftUseCase;
    }

    @PostMapping
    public ResponseEntity<ShiftResult> open(@RequestHeader("X-Cashier") String cashier,
            @Valid @RequestBody OpenShiftRequest request) {
        ShiftResult result = openShiftUseCase.execute(request.toCommand(cashier));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{shiftId}/close")
    public ResponseEntity<ShiftResult> close(@PathVariable UUID shiftId, @Valid @RequestBody CloseShiftRequest request) {
        ShiftResult result = closeShiftUseCase.execute(request.toCommand(shiftId));
        return ResponseEntity.ok(result);
    }
}
