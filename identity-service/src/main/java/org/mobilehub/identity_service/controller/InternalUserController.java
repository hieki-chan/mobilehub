package org.mobilehub.identity_service.controller;

import lombok.RequiredArgsConstructor;
import org.mobilehub.identity_service.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow();

        return new UserDto(user.getId(), user.getEmail());
    }

    record UserDto(Long id, String email) {}
}
