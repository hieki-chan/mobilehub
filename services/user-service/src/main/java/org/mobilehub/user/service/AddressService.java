package org.mobilehub.user.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.user.dto.request.CreateAddressRequest;
import org.mobilehub.user.dto.response.AddressResponse;
import org.mobilehub.user.entity.Address;
import org.mobilehub.user.mapper.AddressMapper;
import org.mobilehub.user.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {

    AddressRepository addressRepository;
    AddressMapper addressMapper;

    public AddressResponse createAddress(CreateAddressRequest request, String userId) {
        Address address = addressMapper.toAddress(request, userId);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponse(savedAddress);
    }

    public boolean removeAddressFromUser(Long addressId, String userId){
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or not owned by user"));

        addressRepository.delete(address);
        return true;
    }
}
