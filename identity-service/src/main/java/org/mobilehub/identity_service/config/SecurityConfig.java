package org.mobilehub.identity_service.config;

import lombok.RequiredArgsConstructor;
import org.mobilehub.identity_service.handler.OAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers(
                          //      "/auth/**"
                                "/auth/register",
                                "/auth/verify",
                                "/auth/resend-otp",
                                "/auth/token",
                                "/auth/validate",
                                "/auth/authenticate",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/auth/reset-password/validate"
                                ).permitAll()

                        .requestMatchers("/admin/**").permitAll()

                        .requestMatchers("/users/**").permitAll()

                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth -> oauth
//                        .loginPage("/oauth2/authorization/google")
//                        .successHandler(oAuth2SuccessHandler)
//                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
