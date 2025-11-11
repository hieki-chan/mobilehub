package org.mobilehub.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAddressRequest {

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    private Boolean isDefault;
}
