package org.mobilehub.identity.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.identity.dto.request.LoginRequest;
import org.mobilehub.identity.dto.request.LogoutRequest;
import org.mobilehub.identity.dto.response.LoginResponse;
import org.mobilehub.identity.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

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
}
