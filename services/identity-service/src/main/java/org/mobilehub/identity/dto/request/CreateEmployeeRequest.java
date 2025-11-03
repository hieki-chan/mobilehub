package org.mobilehub.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEmployeeRequest {
    @NotBlank
    @Email
    String email;
    @NotBlank
    String username;
    @NotBlank
    String password;
}
