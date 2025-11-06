package org.mobilehub.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mobilehub.user.dto.request.CreateAddressRequest;
import org.mobilehub.user.dto.response.AddressResponse;
import org.mobilehub.user.entity.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "userId", source = "userId")
    Address toAddress(CreateAddressRequest createAddressRequest, Long userId);

    AddressResponse toAddressResponse(Address address);
}