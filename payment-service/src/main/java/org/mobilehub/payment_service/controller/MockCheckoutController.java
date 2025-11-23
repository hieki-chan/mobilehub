package org.mobilehub.payment_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MockCheckoutController {

    @GetMapping("/mock/checkout")
    public String checkoutPage(
            @RequestParam String paymentId,
            @RequestParam String returnUrl
    ) {
        return "mock-checkout";
    }
}
