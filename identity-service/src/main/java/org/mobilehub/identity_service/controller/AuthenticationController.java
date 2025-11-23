package org.mobilehub.identity_service.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.identity_service.dto.request.*;
import org.mobilehub.identity_service.dto.response.LoginResponse;
import org.mobilehub.identity_service.dto.response.UserResponse;
import org.mobilehub.identity_service.service.AuthenticationService;
import org.mobilehub.identity_service.service.GoogleAuthService;
import org.mobilehub.identity_service.service.MailService;
import org.mobilehub.identity_service.service.UserService;
import org.mobilehub.identity_service.util.MailHtml;
import org.mobilehub.shared.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class AuthenticationController {

    AuthenticationService authenticationService;
    GoogleAuthService googleAuthService;
    UserService userService;
    MailService mailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserRequest registerRequest) {
        if(userService.getUserByEmail(registerRequest.getEmail()) != null)
            return ResponseEntity.badRequest().body("Tài khoản đã được sử dụng");

        var otp = mailService.generateAndSendOtp(registerRequest.getEmail());
        authenticationService.requestRegistration(registerRequest, otp);

        return ResponseEntity.ok("OTP has been sent to email");
    }

    @PostMapping("/verify")
    public ApiResponse<UserResponse> verifyRegistration(@Valid @RequestBody VerifyOTPRequest request) {
        //boolean ok = mailService.verify(request.getEmail(), request.getOtp());
        // TODO: create new user
        var response = authenticationService.validateRegistration(request.getEmail(), request.getOtp());
        if(!response.isValid()) {
            return ApiResponse.<UserResponse>builder()
                    .message(response.message())
                    .build();
        }

        var userResponse = authenticationService.register(response.registration());
        return ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .message(response.message())
                .build();
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody @Valid ResendOTPRequest req) {
        // generate new otp
        var otp = mailService.generateOtp();
        var pending = authenticationService.resendRegistration(req.getEmail(), otp);
        if (!pending) {
            return ResponseEntity.status(400).body("No pending registration for this email.");
        }

        mailService.sendOtp(req.getEmail(), otp);
        return ResponseEntity.ok("OTP resent to email: " + req.getEmail());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginRequest loginRequest) {
        var response = authenticationService.authenticate(loginRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        String email = request.getEmail();
        if(userService.getUserByEmail(email) == null)
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản: " + email);

        var jwtResetToken = authenticationService.requestResetPassword(email);

        String resetUrl = "localhost:5173/reset-password?token=" + jwtResetToken;

        String htmlBody = MailHtml.buildResetPasswordHtmlBody(resetUrl, 100);

        mailService.sendEmail(email, "Reset password", htmlBody, true);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        if(!authenticationService.resetPassword(request.getJwtResetToken(), request.getNewPassword()))
            return ResponseEntity.badRequest().body("Fail to reset password");

        return ResponseEntity.ok("New password has been set");
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<?> validateJwtResetPasswordToken(@RequestParam String token) {
        return ResponseEntity.ok().body(authenticationService.validateJwtResetPasswordToken(token));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateAccessToken(@RequestParam String token) {
        boolean valid = authenticationService.validateAccessToken(token);
        return ResponseEntity.ok(valid ? "Valid" : "Invalid");
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            var payload = googleAuthService.verifyIdToken(request.getIdToken());
            return ResponseEntity.ok(authenticationService.loginWithGoogle(payload));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        authenticationService.logout(logoutRequest);
        return ResponseEntity.ok().body(HttpStatus.OK);
    }
}
