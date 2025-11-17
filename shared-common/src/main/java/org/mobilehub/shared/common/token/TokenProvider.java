package org.mobilehub.shared.common.token;

import java.text.ParseException;

public interface TokenProvider {
    String generateToken(String subject);
    String generateToken(String subject, ClaimSet claimSet);
    boolean validateToken(String token);
    String extractSubject(String token) throws ParseException;
    Object extractClaim(String token, String name) throws ParseException;
}
