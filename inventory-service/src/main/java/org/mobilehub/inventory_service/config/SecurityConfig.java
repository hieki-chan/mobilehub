package org.mobilehub.inventory_service.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@SuppressWarnings("unused")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/public/**",
                                "/inventory/**"
                        ).permitAll()

                        // ✅ đúng theo controller @RequestMapping("/inventory")
                        .requestMatchers(HttpMethod.POST,
                                "/inventory/reservations/*/commit",
                                "/inventory/reservations/*/release",
                                "/inventory/**"
                        ).permitAll()

                        // (tuỳ bạn) cho reserve public luôn nếu cần
                        .requestMatchers(HttpMethod.POST, "/inventory/reservations").permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
