package org.mobilehub.customer_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAddressRequest {
    @NotBlank
    String fullName;

    @NotBlank
    String phoneNumber;

    // address
    @NotBlank
    String province;

    @NotBlank
    String district;

    @NotBlank
    String ward;

    @NotBlank
    String addressDetail;

    // ggmap
    BigDecimal latitude;
    BigDecimal longitude;

    Boolean isDefault = false;
}
