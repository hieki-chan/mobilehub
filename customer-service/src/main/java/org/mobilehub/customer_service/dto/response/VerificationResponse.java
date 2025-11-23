package org.mobilehub.customer_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class VerificationResponse {
    Boolean status;
    String cccdNo;
    String fullName;
    LocalDate dateOfBirth;
    String sex;
    String placeOfResidence;
}
