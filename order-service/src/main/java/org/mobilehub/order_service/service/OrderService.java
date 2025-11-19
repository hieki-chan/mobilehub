package org.mobilehub.order_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.client.CustomerClient;
import org.mobilehub.order_service.client.ProductClient;
import org.mobilehub.order_service.client.UserClient;
import org.mobilehub.order_service.dto.request.*;
import org.mobilehub.order_service.dto.response.OrderResponse;
import org.mobilehub.order_service.dto.response.OrderSummaryResponse;
import org.mobilehub.order_service.entity.*;
import org.mobilehub.order_service.exception.OrderCannotBeCancelledException;
import org.mobilehub.order_service.exception.OrderNotFoundException;
import org.mobilehub.order_service.Mapper.OrderMapper;
import org.mobilehub.order_service.kafka.OrderEventPublisher;
import org.mobilehub.order_service.repository.OrderRepository;
import org.mobilehub.shared.contracts.order.OrderCreatedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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


    public OrderResponse cancelOrder(Long orderId, Long userId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(!order.getUserId().equals(userId))
            throw new OrderCannotBeCancelledException("no access" + userId);

        if(order.getStatus() != OrderStatus.PENDING)
            throw new OrderCannotBeCancelledException(order.getStatus().toString());

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelReason);
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    public boolean confirmOrder(Long orderId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() != OrderStatus.PENDING)
            return false;

        order.setStatus(OrderStatus.SHIPPING);
        orderRepository.save(order);
        return true;
    }

    public boolean setOrderDelivered(Long orderId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() != OrderStatus.SHIPPING)
            return false;

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
        return true;
    }

    public boolean setOrderFailed(Long orderId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED)
            return false;

        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);
        return true;
    }

    public Page<OrderResponse> getOrdersPaged(int page, int size,
                                              OrderStatus status,
                                              PaymentMethod payment,
                                              ShippingMethod shipping)
    {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<Order> spec = (root, query, cb) -> cb.conjunction();

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        if (payment != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("paymentMethod"), payment));
        }

        if (shipping != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("shippingMethod"), shipping));
        }

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        return orderPage.map(orderMapper::toOrderResponse);
    }

}
