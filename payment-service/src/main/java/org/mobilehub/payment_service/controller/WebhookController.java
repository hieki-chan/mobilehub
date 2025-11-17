package org.mobilehub.payment_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.service.WebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(HttpServletRequest request, @RequestBody String body) {
        Map<String,String> headers = new HashMap<>();
        Enumeration<String> hn = request.getHeaderNames();
        while (hn.hasMoreElements()) {
            String h = hn.nextElement();
            headers.put(h, request.getHeader(h));
        }
        webhookService.handle(body, headers);
        return ResponseEntity.ok().build();
    }
}
