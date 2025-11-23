package org.mobilehub.payment_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.dto.*;
import org.mobilehub.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentIntentController {

    private final PaymentService paymentService;

    @PostMapping("/intents")
    public ResponseEntity<CreateIntentResponse> create(
            @Valid @RequestBody CreateIntentRequest req,
            @RequestHeader(name = "Idempotency-Key", required = false) String idemKey
    ) {
        var res = paymentService.createIntent(req, idemKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/{orderCode}/status")
    public ResponseEntity<PaymentStatusResponse> status(@PathVariable Long orderCode) {
        return ResponseEntity.ok(paymentService.getStatus(orderCode));
    }

    /**
     * PayOS prod-only không hỗ trợ manual capture.
     * Endpoint giữ lại để không phá contract cũ, nhưng trả 501 rõ ràng.
     */
    @PostMapping("/intents/{paymentId}/capture")
    public ResponseEntity<PaymentStatusResponse> capture(
            @PathVariable Long paymentId,
            @Valid @RequestBody CaptureRequest req
    ) {
        try {
            return ResponseEntity.ok(paymentService.capture(paymentId, req));
        } catch (UnsupportedOperationException ex) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
    }

    /**
     * PayOS prod-only chưa hỗ trợ auto refund (thường cần payout/chi hộ).
     * Trả 501 rõ ràng.
     */
    @PostMapping("/refunds")
    public ResponseEntity<PaymentStatusResponse> refund(@Valid @RequestBody RefundRequest req) {
        try {
            return ResponseEntity.ok(paymentService.refund(req));
        } catch (UnsupportedOperationException ex) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
    }
}
