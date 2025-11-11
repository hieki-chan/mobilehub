package org.mobilehub.order_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.order_service.entity.Order;
import org.mobilehub.order_service.entity.OrderStatus;
import org.mobilehub.order_service.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final String TOPIC = "payment.events";
    private final OrderRepository orders;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = TOPIC, groupId = "order-service")
    @Transactional
    public void onPaymentEvent(String message) {
        try {
            JsonNode root = mapper.readTree(message);
            String type = root.path("type").asText();           // PAYMENT_SUCCEEDED / PAYMENT_FAILED / PAYMENT_CANCELED
            long orderCode = root.path("orderCode").asLong();   // = orderId

            Order order = orders.findById(orderCode).orElse(null);
            if (order == null) {
                log.warn("[payment.events] order id={} không tồn tại", orderCode);
                return;
            }

            switch (type) {
                case "PAYMENT_SUCCEEDED" -> {
                    if (order.getStatus() == OrderStatus.PAID) return; // idempotent
                    order.setStatus(OrderStatus.PAID);
                    orders.save(order);
                    log.info("[payment.events] order {} -> PAID", order.getId());
                }
                case "PAYMENT_CANCELED" -> {
                    if (order.getStatus() == OrderStatus.PAID
                            || order.getStatus() == OrderStatus.SHIPPED
                            || order.getStatus() == OrderStatus.DELIVERED) {
                        log.warn("[payment.events] CANCELLED đến muộn, order {} đã {}", order.getId(), order.getStatus());
                        return;
                    }
                    order.setStatus(OrderStatus.CANCELLED);
                    orders.save(order);
                    log.info("[payment.events] order {} -> CANCELLED", order.getId());
                }
                case "PAYMENT_FAILED" -> {
                    if (order.getStatus() == OrderStatus.PAID
                            || order.getStatus() == OrderStatus.SHIPPED
                            || order.getStatus() == OrderStatus.DELIVERED) {
                        log.warn("[payment.events] FAILED đến muộn, order {} đã {}", order.getId(), order.getStatus());
                        return;
                    }
                    order.setStatus(OrderStatus.FAILED);
                    orders.save(order);
                    log.info("[payment.events] order {} -> FAILED", order.getId());
                }
                default -> log.debug("[payment.events] bỏ qua type={}", type);
            }
        } catch (Exception e) {
            log.error("[payment.events] lỗi xử lý: {}", e.getMessage(), e);
        }
    }
}
