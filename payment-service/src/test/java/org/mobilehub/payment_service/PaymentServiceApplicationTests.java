package org.mobilehub.payment_service;

import org.junit.jupiter.api.Test;
import org.mobilehub.payment_service.entity.CaptureMethod;
import org.mobilehub.payment_service.dto.CreateIntentRequest;
import org.mobilehub.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentServiceApplicationTests {

    @Autowired
    PaymentService paymentService;

    @Test
    void create_intent_idempotent() {
        var req = new CreateIntentRequest(123L, new BigDecimal("100000"), "VND", CaptureMethod.AUTOMATIC, "WALLET", "https://return", "PAYOS");
        var r1 = paymentService.createIntent(req, "abc");
        var r2 = paymentService.createIntent(req, "abc");
        assertThat(r1.paymentId()).isEqualTo(r2.paymentId());
    }
}
