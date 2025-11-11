package org.mobilehub.identity_service.service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.identity_service.dto.request.LoginRequest;
import org.mobilehub.identity_service.dto.request.LogoutRequest;
import org.mobilehub.identity_service.dto.request.RegisterUserRequest;
import org.mobilehub.identity_service.dto.response.LoginResponse;
import org.mobilehub.identity_service.dto.response.UserResponse;
import org.mobilehub.identity_service.entity.Role;
import org.mobilehub.identity_service.entity.User;
import org.mobilehub.identity_service.exception.UserException;
import org.mobilehub.identity_service.mapper.UserMapper;
import org.mobilehub.identity_service.repository.UserRepository;
import org.mobilehub.shared.common.token.ClaimSet;
import org.mobilehub.shared.common.token.TokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    UserMapper userMapper;

    PasswordEncoder passwordEncoder;
    TokenProvider tokenProvider;

    public UserResponse register(RegisterUserRequest registerUserRequest) {
        User user = userMapper.toUser(registerUserRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        // check if user exists
        var existingUser = userRepository.findByEmail(registerUserRequest.getEmail());
        if(existingUser .isPresent())
            throw new UserException("User with email" + registerUserRequest.getEmail() + " is already in use");
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

    public boolean logout(LogoutRequest logoutRequest) {
        return true;
    }

    public boolean validate(String token) {
        return tokenProvider.validateToken(token);
    }


    private ClaimSet buildUserClaim(User user) {
        ClaimSet claimSet = new ClaimSet();

        claimSet.add("role", user.getRole());

        return claimSet;
    }
}
