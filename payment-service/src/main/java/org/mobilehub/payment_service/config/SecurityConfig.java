// src/main/java/org/mobilehub/payment_service/config/SecurityConfig.java
package org.mobilehub.payment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/api/payments/webhook").permitAll()
                .anyRequest().permitAll() // DEV: mở hết; PROD: đổi thành authenticated()
        );
        http.httpBasic(Customizer.withDefaults()); // hoặc bỏ nếu dùng JWT
        return http.build();
    }
}
