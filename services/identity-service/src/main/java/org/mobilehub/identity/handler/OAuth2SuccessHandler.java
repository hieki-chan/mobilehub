package org.mobilehub.identity.handler;

import lombok.RequiredArgsConstructor;
import org.mobilehub.shared.common.token.JwtTokenProvider;
import org.mobilehub.shared.common.token.TokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String email = user.getAttribute("email");

        String token = tokenProvider.generateToken(email);

        System.out.println("token: " + token);

        //response.sendRedirect("http://localhost:8081/address/me?token=" + token);
        response.sendRedirect("http://localhost:8080/identity/auth");
    }
}
