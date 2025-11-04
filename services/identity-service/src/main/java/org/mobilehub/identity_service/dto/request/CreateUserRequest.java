package org.mobilehub.identity_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mobilehub.identity_service.entity.Role;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    @NotBlank
    @Email
    String email;
    @NotBlank
    String username;
    @NotBlank
    String password;
    @NotBlank
    Role role;
}
