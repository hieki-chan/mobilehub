package org.mobilehub.customer_service.dto.response;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    Long id;

    String fullName;

    String phoneNumber;

    // address
    String province;

    String district;

    String ward;

    String addressDetail;

    // ggmap
    BigDecimal latitude;
    BigDecimal longitude;

    Boolean isDefault;
}
