package org.mobilehub.order_service.config;

import org.mobilehub.shared.common.token.JwtTokenProvider;
import org.mobilehub.shared.common.token.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${internal.api-key}") String internalApiKey
    ) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/orders/**",
                                "/admin/orders/**",
                                "/internal/**"          // ✅ allow (gated by API key filter)
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        // ✅ Guard /internal/** bằng API key
        http.addFilterBefore(
                new InternalApiKeyFilter(internalApiKey),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey =
                new SecretKeySpec(JwtTokenProvider.SIGNER_KEY.getBytes(), "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public TokenProvider tokenProvider() {
        return new JwtTokenProvider();
    }
}
