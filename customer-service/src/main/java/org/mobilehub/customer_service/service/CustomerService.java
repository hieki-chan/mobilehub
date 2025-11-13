package org.mobilehub.customer_service.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.customer_service.client.UserClient;
import org.mobilehub.customer_service.dto.request.CreateAddressRequest;
import org.mobilehub.customer_service.dto.request.UpdateAddressRequest;
import org.mobilehub.customer_service.dto.response.AddressResponse;
import org.mobilehub.customer_service.dto.response.DeletedAddressResponse;
import org.mobilehub.customer_service.entity.Address;
import org.mobilehub.customer_service.entity.Customer;
import org.mobilehub.customer_service.mapper.AddressMapper;
import org.mobilehub.customer_service.repository.AddressRepository;
import org.mobilehub.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;
    AddressRepository addressRepository;
    AddressMapper addressMapper;

    UserClient  userClient;

    @Transactional
    public AddressResponse createAddress(CreateAddressRequest request, Long userId) {
        if(!userClient.exists(userId)) {
            throw new RuntimeException("Invalid user with id " + userId);
        }

        Customer customer = customerRepository.findById(userId).orElse(null);

        if(customer == null) {
            customer = new Customer();
            customer.setId(userId);
            customerRepository.save(customer);
        }

        Address address = addressMapper.toAddress(request);
        address.setCustomer(customer);

        if (Boolean.TRUE.equals(request.getIsDefault()) || customer.getDefaultAddress() == null) {
            customer.setDefaultAddress(address);
        }

        Address saved = addressRepository.save(address);
        return addressMapper.toAddressResponse(saved, saved.equals(customer.getDefaultAddress()));
    }

    public void setDefaultAddress(Long userId, Long addressId) {
        Customer customer = findCustomer(userId);
        Address address = findAddressOfUser(userId, addressId);

        customer.setDefaultAddress(address);
        customerRepository.save(customer);
    }

    public AddressResponse updateAddress(Long userId, Long addressId, UpdateAddressRequest request) {
        Customer customer = findCustomer(userId);
        Address address = findAddressOfUser(userId, addressId);

        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setAddressDetail(request.getAddressDetail());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        addressRepository.save(address);

        if (request.getIsDefault() != null && request.getIsDefault()) {
            customer.setDefaultAddress(address);
        } else {
            if (address.equals(customer.getDefaultAddress())) {
                customer.setDefaultAddress(null);
            }
        }

        customerRepository.save(customer);

        boolean isDefault = address.equals(customer.getDefaultAddress());

        return addressMapper.toAddressResponse(address, isDefault);
    }

    public DeletedAddressResponse deleteAddressFromUser(Long userId, Long addressId) {
        Customer customer = findCustomer(userId);
        Address address = findAddressOfUser(userId, addressId);

        if (address.equals(customer.getDefaultAddress())) {
            List<Address> others = addressRepository.findAllByCustomerId(userId)
                    .stream()
                    .filter(a -> !a.getId().equals(addressId))
                    .toList();

            if (!others.isEmpty()) {
                customer.setDefaultAddress(others.getFirst());
            } else {
                customer.setDefaultAddress(null);
            }
        }

        addressRepository.delete(address);
        Customer savedCustomer = customerRepository.save(customer);

        //return default address
        Address defaultAddress = savedCustomer.getDefaultAddress();
        return new DeletedAddressResponse(defaultAddress != null ? defaultAddress.getId() : null);
    }

    public List<AddressResponse> getAddressesFromUser(Long userId) {
        Customer customer = findCustomer(userId);
        return addressRepository.findAllByCustomerId(userId)
                .stream()
                .map(a -> addressMapper.toAddressResponse(a, a.equals(customer.getDefaultAddress())))
                .toList();
    }

    private Customer findCustomer(Long userId)
    {
        return customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Invalid user with id " + userId));
    }

    private Address findAddressOfUser(Long userId,  Long addressId)
    {
        return addressRepository.findByIdAndCustomerId(addressId, userId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Address { %s } not found or not owned by user { %s }" , addressId, userId)));
    }
}
