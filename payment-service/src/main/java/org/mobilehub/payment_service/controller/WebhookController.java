package org.mobilehub.payment_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.payment_service.exception.InvalidSignatureException;
import org.mobilehub.payment_service.exception.NotFoundException;
import org.mobilehub.payment_service.service.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestBody String body,
            @RequestHeader Map<String, String> headers
    ) {
        try {
            webhookService.handle(body, headers);
            return ResponseEntity.ok().build();

        } catch (InvalidSignatureException ex) {
            // chữ ký sai -> từ chối luôn, PayOS coi như không hợp lệ
            log.warn("[payment.webhook] invalid signature, reject");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (NotFoundException ex) {
            // nếu order/payment chưa kịp tạo mà webhook tới sớm
            // trả 404 sẽ khiến PayOS retry; giữ để debug rõ.
            log.warn("[payment.webhook] not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception ex) {
            // lỗi server -> PayOS retry, đúng semantics
            log.error("[payment.webhook] handle error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
