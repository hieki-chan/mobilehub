package org.mobilehub.order_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.client.CustomerClient;
import org.mobilehub.order_service.client.ProductClient;
import org.mobilehub.order_service.client.UserClient;
import org.mobilehub.order_service.dto.request.OrderCancelRequest;
import org.mobilehub.order_service.dto.request.OrderCreateRequest;
import org.mobilehub.order_service.dto.request.OrderItemRequest;
import org.mobilehub.order_service.dto.request.OrderUpdateStatusRequest;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.dto.response.OrderSummaryResponse;
import org.mobilehub.order_service.entity.Order;
import org.mobilehub.order_service.entity.OrderItem;
import org.mobilehub.order_service.entity.OrderStatus;
import org.mobilehub.order_service.exception.OrderCannotBeCancelledException;
import org.mobilehub.order_service.exception.OrderNotFoundException;
import org.mobilehub.order_service.Mapper.OrderMapper;
import org.mobilehub.order_service.kafka.OrderEventPublisher;
import org.mobilehub.order_service.repository.OrderRepository;
import org.mobilehub.shared.contracts.order.OrderCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final UserClient userClient;
    private final CustomerClient customerClient;
    private final ProductClient productClient;

    private final OrderEventPublisher publisher;

    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        if(!userClient.exists(userId))
            throw new RuntimeException("user is invalid" +  userId);

        Order order = orderMapper.toOrder(userId, request);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());

        // shipping address
        var address = customerClient.getAddress(request.getAddressId());
        order.setShippingAddress(address.toString());

        return setOrderItems(order, request.getItems());
    }

    private OrderResponse setOrderItems(Order order, List<OrderItemRequest> orderItems)
    {
        var snapShots = productClient.getProductsSnapshot(orderMapper.toRequestList(orderItems));

        List<OrderItem> items = new ArrayList<>();

        for (int i = 0; i < snapShots.size(); i++) {
            var snapshot = snapShots.get(i);
            var orderItem = orderItems.get(i);
            OrderItem item = orderMapper.toOrderItem(snapshot);
            item.setProductId(orderItem.getProductId());
            item.setQuantity(orderItem.getQuantity());
            item.setOriginalPrice(snapshot.getPrice());
            item.setFinalPrice(snapshot.getDiscountedPrice());
            item.setOrder(order);
            items.add(item);
        }

        order.setItems(items);
        var savedOrder = orderRepository.save(order);

        // send event
        publisher.publish(new OrderCreatedEvent(
                savedOrder.getId(),
                order.getUserId(),
                "??",
                orderItems.stream()
                        .map(o
                                -> new OrderCreatedEvent.Item(o.getProductId(), o.getVariantId(), o.getQuantity()))
                        .toList()
        ));

        return orderMapper.toOrderResponse(savedOrder);
    }


    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(orderMapper::toOrderResponse).toList();
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
