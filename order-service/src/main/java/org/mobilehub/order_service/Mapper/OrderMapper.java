package org.mobilehub.order_service.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mobilehub.order_service.dto.request.OrderCreateRequest;
import org.mobilehub.order_service.dto.request.OrderItemRequest;
import org.mobilehub.order_service.dto.request.ProductSnapshotRequest;
import org.mobilehub.order_service.dto.response.OrderItemResponse;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.dto.response.OrderSummaryResponse;
import org.mobilehub.order_service.dto.response.ProductSnapshotResponse;
import org.mobilehub.order_service.entity.Order;
import org.mobilehub.order_service.entity.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Request -> Order
    // Các field hệ thống (items/status/address/reservation/...) sẽ được gán trong service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "reservationId", ignore = true)
    @Mapping(target = "reservedUntil", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "paymentCode", ignore = true)
    Order toOrder(Long userId, OrderCreateRequest request);

    // Snapshot -> OrderItem
    // productId/variantId/quantity sẽ set từ request trong service để không lệch
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "variantId", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    OrderItem toOrderItem(ProductSnapshotResponse snapshot);

    // List request -> list ProductSnapshotRequest (gửi sang product-service)
    List<ProductSnapshotRequest> toRequestList(List<OrderItemRequest> orderItems);

    // Entity -> OrderResponse (full)
    @Mapping(target = "items", source = "items")
    OrderResponse toOrderResponse(Order order);

    // OrderItem -> OrderItemResponse (MapStruct tự map theo tên field)
    // variantId đã có trong cả entity và response nên sẽ map đúng
    @Mapping(target = "variantId", source = "variantId")
    OrderItemResponse toOrderItemResponse(OrderItem item);

    // List items -> list response
    List<OrderItemResponse> toOrderItemResponses(List<OrderItem> items);

    // Entity -> Summary
    OrderSummaryResponse toSummaryResponse(Order order);
}
