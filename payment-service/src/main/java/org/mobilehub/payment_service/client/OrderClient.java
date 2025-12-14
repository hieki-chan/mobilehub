package org.mobilehub.payment_service.client;

import org.mobilehub.payment_service.client.config.OrderFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${services.order.base-url}", configuration = OrderFeignConfig.class)

public interface OrderClient {

    @GetMapping("/internal/orders/{orderId}/reservation")
    ReservationDto getReservation(@PathVariable Long orderId);

    record ReservationDto(Long orderId, String reservationId) {}

    @GetMapping("/internal/orders/{orderId}")
    OrderDto getOrder(@PathVariable Long orderId);

    record OrderDto(Long id, Long userId) {}
}
