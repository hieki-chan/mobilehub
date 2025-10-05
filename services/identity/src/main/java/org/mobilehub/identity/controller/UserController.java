package org.mobilehub.identity.controller;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.identity.dto.request.RegisterUserRequest;
import org.mobilehub.identity.dto.response.UserResponse;
import org.mobilehub.identity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest registerRequest) {
        var userResponse = userService.register(registerRequest);

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{userId}")
    ResponseEntity<UserResponse> getUser(@PathVariable("userId") String userId) {
        var userResponse = userService.getUser(userId);
        return ResponseEntity.ok(userResponse);
    }
}
