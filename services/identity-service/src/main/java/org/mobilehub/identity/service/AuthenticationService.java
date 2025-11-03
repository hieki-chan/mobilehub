package org.mobilehub.identity.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.identity.dto.request.LoginRequest;
import org.mobilehub.identity.dto.request.LogoutRequest;
import org.mobilehub.identity.dto.request.RegisterUserRequest;
import org.mobilehub.identity.dto.response.LoginResponse;
import org.mobilehub.identity.dto.response.UserResponse;
import org.mobilehub.identity.entity.User;
import org.mobilehub.identity.exception.UserException;
import org.mobilehub.identity.mapper.UserMapper;
import org.mobilehub.identity.repository.UserRepository;
import org.mobilehub.shared.common.token.ClaimSet;
import org.mobilehub.shared.common.token.TokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

        claimSet.add("roles", user.getRole());

        return claimSet;
    }
}
