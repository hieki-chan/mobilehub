package org.mobilehub.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.order_service.dto.request.OrderCreateRequest;
import org.mobilehub.order_service.dto.request.OrderItemRequest;
import org.mobilehub.order_service.entity.InstallmentOrderMapping;
import org.mobilehub.order_service.entity.PaymentMethod;
import org.mobilehub.order_service.entity.ShippingMethod;
import org.mobilehub.order_service.repository.InstallmentOrderMappingRepository;
import org.mobilehub.order_service.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstallmentOrderCreateListener {

    private static final String TOPIC = "installment-order-create";
    private static final String GROUP = "order-service-installment";

    private final OrderService orderService;
    private final InstallmentOrderMappingRepository mappingRepository;

    @KafkaListener(
            topics = TOPIC,
            groupId = GROUP
    )
    public void handleInstallmentOrderCreate(InstallmentOrderCreateMessage msg) {
        if (msg == null) {
            log.warn("[order] Receive null message - skip");
            return;
        }

        Long appId = msg.getApplicationId();
        log.info("[order] Receive installment order create message, appId={}", appId);

        if (appId == null) {
            log.warn("[order] applicationId is null - skip");
            return;
        }

        // 1) Idempotent: applicationId này đã có order thì bỏ qua
        if (mappingRepository.existsByApplicationId(appId)) {
            log.info("[order] Order for application {} already exists, skip", appId);
            return;
        }

        // 2) Validate tối thiểu để tránh tạo đơn rác
        if (msg.getUserId() == null) {
            log.warn("[order] userId is null for application {} - skip", appId);
            return;
        }
        if (msg.getAddressId() == null) {
            log.warn("[order] addressId is null for application {} - skip", appId);
            return;
        }
        if (msg.getItems() == null || msg.getItems().isEmpty()) {
            log.warn("[order] items is empty for application {} - skip", appId);
            return;
        }

        // 3) Map Kafka message -> OrderCreateRequest (an toàn enum)
        OrderCreateRequest req = mapToOrderCreateRequest(msg);

        // 4) Tạo order theo flow riêng trả góp
        var response = orderService.createOrderFromInstallment(msg.getUserId(), req);

        // 5) Lưu mapping applicationId <-> orderId để idempotent
        InstallmentOrderMapping mapping = InstallmentOrderMapping.builder()
                .applicationId(appId)
                .orderId(response.getId())
                .build();
        mappingRepository.save(mapping);

        log.info("[order] Created order {} for application {}", response.getId(), appId);
    }

    private OrderCreateRequest mapToOrderCreateRequest(InstallmentOrderCreateMessage msg) {
        OrderCreateRequest req = new OrderCreateRequest();

        // NOTE:
        // - Tránh valueOf trực tiếp vì message có thể gửi "DELIVERY" trong khi enum chỉ có STANDARD/EXPRESS
        // - Tránh crash consumer gây retry vô hạn
        req.setPaymentMethod(mapPaymentMethod(msg.getPaymentMethod()));
        req.setShippingMethod(mapShippingMethod(msg.getShippingMethod()));

        req.setNote(msg.getNote());
        req.setAddressId(msg.getAddressId());

        List<OrderItemRequest> items = msg.getItems().stream()
                .map(this::mapItem)
                .toList();
        req.setItems(items);

        return req;
    }

    private OrderItemRequest mapItem(InstallmentOrderCreateMessage.Item i) {
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(i.getProductId());
        item.setVariantId(i.getVariantId());
        item.setQuantity(i.getQuantity());
        return item;
    }

    /**
     * Mapping shipping method từ message -> enum của order-service.
     * Hiện enum chỉ có: STANDARD, EXPRESS
     * Nên mọi biến thể "DELIVERY" sẽ được map về STANDARD.
     */
    private ShippingMethod mapShippingMethod(String raw) {
        if (raw == null) return ShippingMethod.STANDARD;

        String v = raw.trim().toUpperCase();

        return switch (v) {
            case "STANDARD", "STD" -> ShippingMethod.STANDARD;
            case "EXPRESS", "FAST" -> ShippingMethod.EXPRESS;

            // installment/FE hay gửi kiểu này
            case "DELIVERY", "HOME_DELIVERY", "SHIP", "SHIPPING" -> ShippingMethod.STANDARD;

            default -> {
                try {
                    yield ShippingMethod.valueOf(v);
                } catch (Exception ex) {
                    log.warn("[order] Unknown shippingMethod='{}' -> fallback STANDARD", raw);
                    yield ShippingMethod.STANDARD;
                }
            }
        };
    }

    /**
     * Mapping payment method an toàn để tránh crash consumer nếu lệch enum.
     * Bạn có thể đổi fallback theo nghiệp vụ (VD: INSTALLMENT nếu enum có).
     */
    private PaymentMethod mapPaymentMethod(String raw) {
        if (raw == null) {
            log.warn("[order] paymentMethod is null -> fallback COD");
            return PaymentMethod.COD;
        }

        String v = raw.trim().toUpperCase();

        try {
            return PaymentMethod.valueOf(v);
        } catch (Exception ex) {
            log.warn("[order] Unknown paymentMethod='{}' -> fallback COD", raw);
            return PaymentMethod.COD;
        }
    }
}
