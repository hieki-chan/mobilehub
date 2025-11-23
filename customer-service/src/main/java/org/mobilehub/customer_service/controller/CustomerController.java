package org.mobilehub.customer_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.customer_service.dto.response.VerificationResponse;
import org.mobilehub.customer_service.dto.response.VerifyResponse;
import org.mobilehub.customer_service.service.CustomerService;
import org.mobilehub.customer_service.util.UserAccess;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class CustomerController {

    CustomerService customerService;

    @PostMapping("/customers/verify")
    public ResponseEntity<VerifyResponse> extract(@RequestParam("frontImage") MultipartFile frontImage) {
        VerifyResponse response = customerService.verifyCCCD(frontImage, UserAccess.getPrincipalId());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/customers/{customerId}/verification-status")
    public ResponseEntity<VerificationResponse> getVerificationStatus(@PathVariable Long customerId) {
        UserAccess.validateUserAccess(customerId);
        var isVerified = customerService.checkVerified(customerId);
        return ResponseEntity.ok(isVerified);
    }

    @GetMapping("/customers/verification-status")
    public ResponseEntity<VerificationResponse> getVerificationStatus() {
        var isVerified = customerService.checkVerified(UserAccess.getPrincipalId());
        return ResponseEntity.ok(isVerified);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/customers/{customerId}/verification-status")
    public ResponseEntity<VerificationResponse> getVerificationStatusAmin(@PathVariable Long customerId) {
        var isVerified = customerService.checkVerified(customerId);
        return ResponseEntity.ok(isVerified);
    }
}
