package org.mobilehub.identity_service.config;

import lombok.RequiredArgsConstructor;
import org.mobilehub.identity_service.entity.Role;
import org.mobilehub.identity_service.entity.User;
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
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);

        if (!adminExists) {
            User admin = User.builder()
                    .email("admin@mobilehub.com")
                    .username("admin")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
            System.out.println("Default admin account created");
        } else {
            System.out.println("Admin already exists");
        }
    }
}
