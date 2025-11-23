package org.mobilehub.customer_service.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.customer_service.dto.request.CreateAddressRequest;
import org.mobilehub.customer_service.dto.response.AddressResponse;
import org.mobilehub.customer_service.entity.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(CreateAddressRequest createAddressRequest);

    AddressResponse toAddressResponse(Address address, Boolean isDefault);
}