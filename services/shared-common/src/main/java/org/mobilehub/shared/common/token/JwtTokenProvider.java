package org.mobilehub.shared.common.token;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class JwtTokenProvider implements TokenProvider {
    public static final String SIGNER_KEY = "koNAzoN/cNgpNBC6N7JFzl8Gkycz3ryA39mWjs4q1N7YPw+dB68UcwPQPNhfY3iiIE9aTl9kC5mX0640CmPGFw";
    private static final String ISSUER = "mobilehub-auth.com";
    private static final long VALID_DURATION = 3600_000 * 12; // 1 hour in ms
    private static final long REFRESHABLE_DURATION = 36_000_000; // 10 hours in ms

//    @NonFinal
//    @Value("${jwt.signerKey}")
//    protected String SIGNER_KEY;
//
//    @NonFinal
//    @Value("${jwt.valid-duration}")
//    protected long VALID_DURATION;
//
//    @NonFinal
//    @Value("${jwt.issuer}")
//    protected String ISSUER;
//
//    @NonFinal
//    @Value("${jwt.refreshable-duration}")
//    protected long REFRESHABLE_DURATION;

    @Override
    public String generateToken(String subject) {
        return generateToken(subject, new ClaimSet());
    }

    // Generate token
    @Override
    public String generateToken(String subject, ClaimSet claimSet) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(ISSUER)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plusMillis(VALID_DURATION)))
                .jwtID(UUID.randomUUID().toString());

        claimSet.asMap().forEach(jwtClaimsSetBuilder::claim);

        JWTClaimsSet jwtClaimsSet =  jwtClaimsSetBuilder.build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    // Validate + Parse Token
    @Override
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

            boolean verified = signedJWT.verify(verifier);

            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            return verified && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractSubject(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
