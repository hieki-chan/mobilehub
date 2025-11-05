package org.mobilehub.identity_service.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.identity_service.dto.request.AdminUpdateUserRequest;
import org.mobilehub.identity_service.dto.request.CreateUserRequest;
import org.mobilehub.identity_service.dto.response.AdminUserResponse;
import org.mobilehub.identity_service.dto.response.UserResponse;
import org.mobilehub.identity_service.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class UserController {

    UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<AdminUserResponse> users = userService.getAllUsersPaged(page, size, sortBy, sortDir);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createEmployeeRequest)
    {
        return ResponseEntity.ok(userService.createUser(createEmployeeRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/employees")
    public ResponseEntity<List<UserResponse>> getAllEmployees() {
        return ResponseEntity.ok(userService.getAllEmployees());
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid AdminUpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa người dùng thành công"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Không thể xóa người dùng"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
        var userResponse = userService.getUser(userId);
        return ResponseEntity.ok(userResponse);
    }
}
