package org.mobilehub.identity_service.config;

import lombok.RequiredArgsConstructor;
import org.mobilehub.identity_service.entity.Role;
import org.mobilehub.identity_service.entity.User;
import org.mobilehub.identity_service.entity.UserStatus;
import org.mobilehub.identity_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandlineAppStartRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ===== Create default ADMIN if not exists =====
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);
        if (!adminExists) {
            User admin = User.builder()
                    .email("admin@mobilehub.com")
                    .username("admin")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(admin);
            System.out.println("Default admin account created");
        } else {
            System.out.println("Admin already exists");
        }

        // ===== Create default USER if not exists =====
        boolean userExists = userRepository.existsByRole(Role.USER);
        if (!userExists) {
            User user = User.builder()
                    .email("user@mobilehub.com")
                    .username("user")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.USER)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(user);
            System.out.println("Default user account created");
        } else {
            System.out.println("User already exists");
        }
    }
}
