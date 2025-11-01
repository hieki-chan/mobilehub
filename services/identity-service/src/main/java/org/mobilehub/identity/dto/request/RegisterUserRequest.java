package org.mobilehub.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest {

    @NotBlank()
    @Email
    private String email;

    @Size(min=4, max=20)
    private String username;

    @Size(min=8, max=20)
    private String password;
}
