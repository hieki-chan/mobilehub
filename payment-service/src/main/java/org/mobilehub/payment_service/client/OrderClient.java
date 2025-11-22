package org.mobilehub.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${services.order}")
public interface OrderClient {

    @GetMapping("/internal/orders/{orderId}/reservation")
    ReservationDto getReservation(@PathVariable Long orderId);

    record ReservationDto(Long orderId, String reservationId) {}
}
