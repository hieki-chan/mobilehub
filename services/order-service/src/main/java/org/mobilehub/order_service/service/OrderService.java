package org.mobilehub.order_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.dto.request.OrderCancelRequest;
import org.mobilehub.order_service.dto.request.OrderCreateRequest;
import org.mobilehub.order_service.dto.request.OrderUpdateStatusRequest;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.dto.response.OrderSummaryResponse;
import org.mobilehub.order_service.entity.Order;
import org.mobilehub.order_service.entity.OrderItem;
import org.mobilehub.order_service.entity.OrderStatus;
import org.mobilehub.order_service.exception.OrderCannotBeCancelledException;
import org.mobilehub.order_service.exception.OrderNotFoundException;
import org.mobilehub.order_service.Mapper.OrderMapper;
import org.mobilehub.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    public OrderResponse createOrder(OrderCreateRequest request) {
        // Tạo entity Order mới
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setShippingAddress("Địa chỉ mặc định " + request.getUserId());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());

        // Tạo danh sách sản phẩm từ request
        List<OrderItem> items = request.getItems().stream()
                .map(i -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(i.getProductId());
                    item.setProductName(i.getProductName());
                    item.setThumbnailUrl(i.getThumbnailUrl());
                    item.setPrice(i.getPrice());
                    item.setQuantity(i.getQuantity());
                    item.setOrder(order);
                    return item;
                })
                .toList();

        order.setItems(items);

        // Tính tổng tiền
        BigDecimal totalAmount = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // Lưu và trả về DTO thông qua mapper
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    /**
     * Lấy chi tiết 1 đơn hàng theo ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toOrderResponse(order);
    }

    /**
     * Lấy danh sách đơn hàng của người dùng
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toSummaryResponse) // ✅ Dùng MapStruct mapper
                .toList();
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    public OrderResponse updateStatus(Long orderId, OrderUpdateStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(request.getStatus());
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    /**
     * Hủy đơn hàng (chỉ khi đang PENDING)
     */
    public OrderResponse cancelOrder(Long orderId, OrderCancelRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderCannotBeCancelledException(order.getStatus().name());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
}
