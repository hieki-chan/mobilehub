package org.mobilehub.identity.dto.request;


import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Email
    private String email;
    private String password;
}
