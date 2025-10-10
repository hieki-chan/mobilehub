package org.mobilehub.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOTPRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String otp;
}
