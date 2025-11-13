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

    Order toOrder(Long userId, OrderCreateRequest request);
    OrderItem toOrderItem(ProductSnapshotResponse snapshot);

    List<ProductSnapshotRequest> toRequestList(List<OrderItemRequest> orderItems);

    // Chuyển từ Entity → DTO chính
    @Mapping(target = "items", source = "items")
    OrderResponse toOrderResponse(Order order);

    // Chuyển từng OrderItem → OrderItemResponse
    OrderItemResponse toOrderItemResponse(OrderItem item);

    // Chuyển danh sách Item
    List<OrderItemResponse> toOrderItemResponses(List<OrderItem> items);

    // Chuyển sang dạng tóm tắt
    OrderSummaryResponse toSummaryResponse(Order order);
}
