package org.mobilehub.identity_service.dto.response;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mobilehub.identity_service.entity.Role;
import org.mobilehub.identity_service.entity.UserStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String email;
    private String username;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
