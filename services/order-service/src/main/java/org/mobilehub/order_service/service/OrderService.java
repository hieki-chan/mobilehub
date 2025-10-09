package org.mobilehub.order_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.Mapper.OrderMapper;
import org.mobilehub.order_service.dto.request.OrderCancelRequest;
import org.mobilehub.order_service.dto.request.OrderCreateRequest;
import org.mobilehub.order_service.dto.request.OrderUpdateStatusRequest;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.dto.response.OrderSummaryResponse;
import org.mobilehub.order_service.entity.Order;
import org.mobilehub.order_service.entity.OrderItem;
import org.mobilehub.order_service.exception.OrderCannotBeCancelledException;
import org.mobilehub.order_service.exception.OrderNotFoundException;
import org.mobilehub.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    public OrderResponse createOrder(OrderCreateRequest request) {

        String shippingAddress = "Địa chỉ mặc định " + request.getUserId();

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Gán danh sách sản phẩm
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
                .collect(Collectors.toList());

        order.setItems(items);

        // Tính tổng tiền
        BigDecimal totalAmount = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        // Lưu vào DB
        Order saved = orderRepository.save(order);
        return orderMapper.toOrderResponse(saved);
    }


    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toOrderResponse(order);
    }


    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }


    public OrderResponse updateStatus(Long orderId, OrderUpdateStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(request.getStatus());
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }


    public OrderResponse cancelOrder(Long orderId, OrderCancelRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!"PENDING".equals(order.getStatus())) {
            throw new OrderCannotBeCancelledException(order.getStatus());
        }

        order.setStatus("CANCELLED");
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
}
