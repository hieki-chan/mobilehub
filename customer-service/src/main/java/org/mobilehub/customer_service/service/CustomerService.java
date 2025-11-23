package org.mobilehub.customer_service.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.customer_service.client.CCCDApiClient;
import org.mobilehub.customer_service.client.UserClient;
import org.mobilehub.customer_service.dto.response.VerificationResponse;
import org.mobilehub.customer_service.dto.response.VerifyResponse;
import org.mobilehub.customer_service.entity.Customer;
import org.mobilehub.customer_service.mapper.CustomerMapper;
import org.mobilehub.customer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;
    CCCDApiClient cccdApiClient;
    UserClient userClient;
    CustomerMapper customerMapper;

    public VerifyResponse verifyCCCD(MultipartFile fontImage, Long customerId)
    {
        Customer customer = customerRepository.findById(customerId)
                .orElse(null);

        // java nguvl, neu = null ms gan ma cu bao cai loi j y cus
       if(customer == null)
       {
           if(!userClient.exists(customerId)) {
               throw new RuntimeException("user is invalid or has no access permission: " + customerId);
           }

           customer = new Customer();
           customer.setId(customerId);
           customerRepository.save(customer);
       }

        VerifyResponse response = cccdApiClient.verifyCCCD(fontImage);
        if(response.getStatus().equalsIgnoreCase("ok"))
        {
            var info = response.getInfo();
            customer.setIdentityNumber(info.getCccdNo());
            customer.setFullName(info.getFullName());
            customer.setSex(info.getSex());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            customer.setDateOfBirth(LocalDate.parse(info.getDateOfBirth(), formatter));
            customer.setPlaceOfResidence(info.getPlaceOfResidence());
            customerRepository.save(customer);
        }

        return response;
    }

    public VerificationResponse checkVerified(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("no access permission: " + customerId));

        return customerMapper.toVerificationResponse(customer);
    }
}
