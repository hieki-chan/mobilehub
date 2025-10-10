package org.mobilehub.identity.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.identity.dto.request.*;
import org.mobilehub.identity.dto.response.LoginResponse;
import org.mobilehub.identity.dto.response.UserResponse;
import org.mobilehub.identity.service.AuthenticationService;
import org.mobilehub.identity.service.MailService;
import org.mobilehub.shared.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;
    MailService mailService;

    Map<String, PendingRegistration> pendingRegistrationMap = new ConcurrentHashMap<>();

    private record PendingRegistration(RegisterUserRequest registration) {
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserRequest registerRequest) {
        mailService.generateAndSendOtp(registerRequest.getEmail());
        pendingRegistrationMap.put(registerRequest.getEmail(), new PendingRegistration(registerRequest));
        System.out.println(pendingRegistrationMap.size());
        //var userResponse = authenticationService.register(registerRequest);

        return ResponseEntity.ok("register");
    }

    @PostMapping("/verify")
    public ApiResponse<UserResponse> verifyRegistration(@Valid @RequestBody VerifyOTPRequest req) {
        boolean ok = mailService.verify(req.getEmail(), req.getOtp());
        if (ok) {
            // TODO: create new user
            var registration = pendingRegistrationMap.get(req.getEmail());
            if(registration == null) {
                return ApiResponse.<UserResponse>builder()
                        .message("Gmail is in valid")
                        .build();
            }
            var userResponse = authenticationService.register(registration.registration());
            return ApiResponse.<UserResponse>builder()
                    .result(userResponse)
                    .message("OTP verified.")
                    .build();
        } else {
            return ApiResponse.<UserResponse>builder()
                    .result(null)
                    .message("Invalid or expired OTP.")
                    .build();
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody @Valid ResendOTPRequest req) {
        var pending = pendingRegistrationMap.get(req.getEmail());
        if (pending == null) {
            return ResponseEntity.status(400).body("No pending registration for this email.");
        }

        // generate new otp
        mailService.generateAndSendOtp(req.getEmail());
        return ResponseEntity.ok("OTP resent to email: " + req.getEmail());
    }

    @PostMapping("/token")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginRequest loginRequest) {
        var response = authenticationService.authenticate(loginRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        authenticationService.logout(logoutRequest);
        return ResponseEntity.ok().body(HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        boolean valid = authenticationService.validate(token);
        return ResponseEntity.ok(valid ? "Valid" : "Invalid");
    }

    @GetMapping
    public String home()
    {
        return "home";
    }
}
