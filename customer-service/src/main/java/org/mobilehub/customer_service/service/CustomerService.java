package org.mobilehub.customer_service.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.customer_service.client.CCCDApiClient;
import org.mobilehub.customer_service.dto.response.VerifyResponse;
import org.mobilehub.customer_service.entity.Customer;
import org.mobilehub.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;
    CCCDApiClient cccdApiClient;

    public VerifyResponse verifyCCCD(MultipartFile fontImage, Long customerId)
    {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("no access permission: " + customerId));

        VerifyResponse response = cccdApiClient.verifyCCCD(fontImage);
        if(response.getStatus().equalsIgnoreCase("ok"))
        {
            customer.setIdentityNumber(response.getInfo().getCccdNo());
            customerRepository.save(customer);
        }

        return response;
    }

    public Boolean isVerified(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("no access permission: " + customerId));

        return customer.getIdentityNumber() != null;
    }
}
