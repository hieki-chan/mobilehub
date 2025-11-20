package org.mobilehub.customer_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CCCDInfo {
    @JsonProperty("Số CCCD")
    private String cccdNo;

    @JsonProperty("Họ và tên")
    private String fullName;

    @JsonProperty("Ngày sinh")
    private String dateOfBirth;

    @JsonProperty("Giới tính")
    private String sex;

    @JsonProperty("Quốc tịch")
    private String nationality;

    @JsonProperty("Quê quán")
    private String placeOfOrigin;

    @JsonProperty("Nơi thường trú")
    private String placeOfResidence;

    @JsonProperty("Giá trị đến")
    private String expiryDate;

    @JsonProperty("Ngày cấp")
    private String issuedDate;

    @JsonProperty("Cơ quan cấp")
    private String issuingAuthority;
}

