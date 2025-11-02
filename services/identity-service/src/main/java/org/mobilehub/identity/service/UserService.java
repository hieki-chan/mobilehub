package org.mobilehub.identity.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.identity.dto.request.CreateEmployeeRequest;
import org.mobilehub.identity.dto.response.UserResponse;
import org.mobilehub.identity.entity.Role;
import org.mobilehub.identity.entity.SignInProvider;
import org.mobilehub.identity.entity.User;
import org.mobilehub.identity.exception.UserException;
import org.mobilehub.identity.mapper.UserMapper;
import org.mobilehub.identity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createEmployee(CreateEmployeeRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email này đã tồn tại!");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserException("Tên người dùng đã tồn tại!");
        }

        User employee = userMapper.toUser(request);
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRole(Role.EMPLOYEE);

        return userMapper.toUserResponse(userRepository.save(employee));
    }

    public List<UserResponse> getAllEmployees() {
        return userRepository.findAllByRole(Role.EMPLOYEE)
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByRole(Role.USER)
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
