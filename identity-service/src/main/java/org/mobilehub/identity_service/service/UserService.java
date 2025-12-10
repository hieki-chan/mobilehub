package org.mobilehub.identity_service.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.identity_service.dto.request.AdminUpdateUserRequest;
import org.mobilehub.identity_service.dto.request.CreateUserRequest;
import org.mobilehub.identity_service.dto.response.AdminUserResponse;
import org.mobilehub.identity_service.dto.response.UserResponse;
import org.mobilehub.identity_service.entity.Role;
import org.mobilehub.identity_service.entity.User;
import org.mobilehub.identity_service.exception.UserException;
import org.mobilehub.identity_service.mapper.UserMapper;
import org.mobilehub.identity_service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public Page<AdminUserResponse> getAllUsersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(userMapper::toAdminUserResponse);
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email này đã tồn tại!");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserException("Tên người dùng đã tồn tại!");
        }

        User employee = userMapper.toUser(request);
        employee.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(employee));
    }

    public UserResponse updateUser(Long id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng với ID: " + id));

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());

        User saved = userRepository.save(user);
        return userMapper.toUserResponse(saved);
    }


    public boolean deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng với ID: " + id));

        if(user.getRole() == Role.ADMIN)
            return false;

        userRepository.delete(user);
        return true;
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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Long getNewUsersThisMonth() {
        return userRepository.countNewUsersThisMonth();
    }
}
