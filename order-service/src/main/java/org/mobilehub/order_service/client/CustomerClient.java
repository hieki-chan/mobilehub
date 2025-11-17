package org.mobilehub.order_service.client;

import org.mobilehub.order_service.dto.response.AddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", url = "${service.customer}")
public interface CustomerClient {

    @GetMapping("/customers/addresses/{addressId}")
    AddressResponse getAddress(@PathVariable("addressId") Long addressId);
}
