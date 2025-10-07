package org.mobilehub.user.config;

import org.mobilehub.shared.common.token.JwtTokenProvider;
import org.mobilehub.shared.common.token.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
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
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)  // disable form login
                .httpBasic(AbstractHttpConfigurer::disable); // disable HTTP Basic

        return http.build();
    }
    @Bean
    public TokenProvider tokenProvider() {
        return new JwtTokenProvider();
    }
}
