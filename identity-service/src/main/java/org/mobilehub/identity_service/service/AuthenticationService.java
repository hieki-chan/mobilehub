package org.mobilehub.identity_service.service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.identity_service.dto.request.LoginRequest;
import org.mobilehub.identity_service.dto.request.LogoutRequest;
import org.mobilehub.identity_service.dto.request.RegisterUserRequest;
import org.mobilehub.identity_service.dto.response.LoginResponse;
import org.mobilehub.identity_service.dto.response.RegistrationResponse;
import org.mobilehub.identity_service.dto.response.UserResponse;
import org.mobilehub.identity_service.entity.Role;
import org.mobilehub.identity_service.entity.User;
import org.mobilehub.identity_service.exception.UserException;
import org.mobilehub.identity_service.mapper.UserMapper;
import org.mobilehub.identity_service.repository.UserRepository;
import org.mobilehub.shared.common.token.ClaimSet;
import org.mobilehub.shared.common.token.TokenProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    UserMapper userMapper;

    PasswordEncoder passwordEncoder;
    TokenProvider tokenProvider;

    Map<String, PendingRegistration> pendingRegistrationMap = new ConcurrentHashMap<>();
    Map<String, ResetToken> pendingPasswordResetMap = new ConcurrentHashMap<>();

    private record PendingRegistration(
            RegisterUserRequest registration,
            OtpEntry otpEntry) {
    }

    public record ResetToken(String token, long expireAt){
        public static final long expireInMinutes = 15;

        public boolean isValid(String token){
            return this.token.equals(token) && Instant.now().toEpochMilli() <= expireAt;
        }
    }

    public void requestRegistration(RegisterUserRequest request, OtpEntry otp) {
        pendingRegistrationMap.put(request.getEmail(), new PendingRegistration(request, otp));
    }

    public RegistrationResponse validateRegistration(String email, String otp){
        var registration = pendingRegistrationMap.get(email);
        if(registration == null)
            return new RegistrationResponse(null, false, "Invalid email");

        var otpEntry = registration.otpEntry();
        if(!Objects.equals(otp, otpEntry.otp()) || !otpEntry.isValid())
            return new RegistrationResponse(null, false, "Invalid or expired OTP.");

        return new RegistrationResponse(registration.registration(), true, "verified.");
    }

    public boolean resendRegistration(String email, OtpEntry otp){
        var registration = pendingRegistrationMap.get(email);
        if(registration == null)
            return false;

        pendingRegistrationMap.put(email, new PendingRegistration(registration.registration(), otp));
        return true;
    }

    public UserResponse register(RegisterUserRequest registerUserRequest) {
        // check if user exists
        var existingUser = userRepository.findByEmail(registerUserRequest.getEmail());
        if(existingUser.isPresent())
            throw new UserException("User with email" + registerUserRequest.getEmail() + " is already in use");

        User user = userMapper.toUser(registerUserRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserException("Email not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UserException("Wrong password");
        }

        return LoginResponse.builder()
                .accessToken(tokenProvider.generateToken(String.valueOf(user.getId()), buildUserClaim(user)))
                .user(userMapper.toUserInfo(user))
                .build();
    }

    public String requestResetPassword(String email)
    {
        String token = UUID.randomUUID().toString();
        String jwtResetToken = tokenProvider.generateToken(email, buildResetPasswordClaim(token));
        long expireAt = Instant.now().plusSeconds(ResetToken.expireInMinutes * 60).toEpochMilli();
        ResetToken resetToken = new ResetToken(token, expireAt);
        pendingPasswordResetMap.put(email, resetToken);

        return jwtResetToken;
    }

    public boolean validateJwtResetPasswordToken(String jwtToken) {
        String email = null;
        try {
            email = tokenProvider.extractSubject(jwtToken);
        } catch (ParseException e) {
            return false;
        }
        return pendingPasswordResetMap.containsKey(email) && tokenProvider.validateToken(jwtToken);
    }

    public boolean resetPassword(String jwtResetToken, String newPassword) {
        String email = null;
        try {
            email = tokenProvider.extractSubject(jwtResetToken);
        } catch (ParseException e) {
            return false;
        }
        if(!pendingPasswordResetMap.containsKey(email))
            return false;

        ResetToken resetToken = pendingPasswordResetMap.get(email);
        System.out.println(resetToken.token);
        String token = null;
        try {
            token = tokenProvider.extractClaim(jwtResetToken, "hiekichan").toString();
        } catch (ParseException e) {
            return false;
        }
        if(!resetToken.isValid(token)) {
            return false;
        }

        String finalEmail = email;
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(String.format("User with email %s not found", finalEmail)));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        pendingPasswordResetMap.remove(email);
        return true;
    }

    public boolean logout(LogoutRequest logoutRequest) {
        return true;
    }

    public boolean validateAccessToken(String token) {
        return tokenProvider.validateToken(token);
    }

    private ClaimSet buildUserClaim(User user) {
        ClaimSet claimSet = new ClaimSet();

        claimSet.add("id", user.getId());
        claimSet.add("role", user.getRole());

        return claimSet;
    }

    private ClaimSet buildResetPasswordClaim(String resetToken) {
        ClaimSet claimSet = new ClaimSet();
        claimSet.add("hiekichan", resetToken);
        return claimSet;
    }

    // scheduled cleanup every minute to remove expired entries
    @Scheduled(fixedRate = 60_000)
    public void cleanup() {
        long now = Instant.now().toEpochMilli();
        pendingRegistrationMap
                .entrySet()
                .removeIf(e -> e.getValue().otpEntry().expireAt() < now);

        pendingPasswordResetMap
                .entrySet()
                .removeIf(e -> e.getValue().expireAt() < now);
    }
}
