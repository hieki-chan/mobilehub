package org.mobilehub.payment_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.dto.*;
import org.mobilehub.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentIntentController {

    private final PaymentService paymentService;

    @PostMapping("/intents")
    public ResponseEntity<CreateIntentResponse> create(@Valid @RequestBody CreateIntentRequest req,
                                                       @RequestHeader(name = "Idempotency-Key", required = false) String idemKey) {
        var res = paymentService.createIntent(req, idemKey);
        return ResponseEntity.status(201).body(res);
    }

    @GetMapping("/{orderCode}/status")
    public ResponseEntity<PaymentStatusResponse> status(@PathVariable Long orderCode) {
        return ResponseEntity.ok(paymentService.getStatus(orderCode));
    }

    @PostMapping("/intents/{paymentId}/capture")
    public ResponseEntity<PaymentStatusResponse> capture(@PathVariable Long paymentId,
                                                         @Valid @RequestBody CaptureRequest req) {
        return ResponseEntity.ok(paymentService.capture(paymentId, req));
    }

    @PostMapping("/refunds")
    public ResponseEntity<PaymentStatusResponse> refund(@Valid @RequestBody RefundRequest req) {
        return ResponseEntity.ok(paymentService.refund(req));
    }
}
