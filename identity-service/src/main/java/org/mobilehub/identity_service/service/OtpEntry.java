package org.mobilehub.identity_service.service;

import java.time.Instant;

public record OtpEntry(String otp, long expireAt) {
    public boolean isValid() {
        return Instant.now().toEpochMilli() <= expireAt;
    }
}