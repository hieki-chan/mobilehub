package org.mobilehub.customer_service.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.customer_service.dto.response.VerificationResponse;
import org.mobilehub.customer_service.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    VerificationResponse toVerificationResponse(Customer customer);
}