package org.mobilehub.identity.config;

import org.mobilehub.shared.common.token.JwtTokenProvider;
import org.mobilehub.shared.common.token.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public TokenProvider tokenProvider() {
        return new JwtTokenProvider();
    }
}
