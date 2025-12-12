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

    @GetMapping({"/webhook", "/webhook/"})
    public ResponseEntity<Void> webhookPing() {
        // PayOS/monitoring thường ping GET để test URL
        return ResponseEntity.ok().build();
    }

    @PostMapping({"/webhook", "/webhook/"})
    public ResponseEntity<Void> webhook(
            @RequestBody(required = false) String body,
            @RequestHeader Map<String, String> headers
    ) {
        log.info("[payos-webhook] HIT POST /api/payments/webhook bodyLen={} headersKeys={}",
                body == null ? 0 : body.length(),
                headers == null ? "null" : headers.keySet()
        );

        // ACK sớm nếu body rỗng
        if (body == null || body.isBlank()) {
            return ResponseEntity.ok().build();
        }

        try {
            webhookService.handle(body, headers);
            return ResponseEntity.ok().build();

        } catch (InvalidSignatureException ex) {
            // Sai chữ ký: trả 401 để PayOS biết request không hợp lệ
            log.warn("[payos-webhook] INVALID SIGNATURE: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (NotFoundException ex) {
            // ⚠️ Quan trọng: webhook không nên trả 404 vì lỗi nội bộ/thiếu mapping
            // Trả 200 để PayOS không retry spam, đồng thời log để bạn xử lý reconcile sau
            log.warn("[payos-webhook] NOT FOUND (ACK 200): {}", ex.getMessage());
            return ResponseEntity.ok().build();

        } catch (Exception ex) {
            // Lỗi không mong muốn: thường vẫn nên trả 200 để tránh PayOS retry liên tục,
            // nhưng nếu bạn muốn PayOS retry thì đổi thành 500.
            log.error("[payos-webhook] ERROR (ACK 200)", ex);
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping({"/webhook/ping", "/webhook/ping/"})
    public ResponseEntity<String> ping(@RequestBody(required = false) String body) {
        return ResponseEntity.ok("OK");
    }
}
