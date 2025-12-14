package org.mobilehub.order_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.order_service.client.CustomerClient;
import org.mobilehub.order_service.client.ProductClient;
import org.mobilehub.order_service.client.UserClient;
import org.mobilehub.order_service.dto.request.*;
import org.mobilehub.order_service.dto.response.*;
import org.mobilehub.order_service.entity.*;
import org.mobilehub.order_service.exception.OrderCannotBeCancelledException;
import org.mobilehub.order_service.exception.OrderNotFoundException;
import org.mobilehub.order_service.Mapper.OrderMapper;
import org.mobilehub.order_service.kafka.OrderEventPublisher;
import org.mobilehub.order_service.repository.OrderItemRepository;
import org.mobilehub.order_service.repository.OrderRepository;
import org.mobilehub.shared.contracts.order.OrderCreatedEvent;
import org.mobilehub.shared.contracts.order.OrderDeliveredEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository      orderRepository;
    private final OrderItemRepository  orderItemRepository;
    private final OrderMapper          orderMapper;

    private final UserClient           userClient;
    private final CustomerClient       customerClient;
    private final ProductClient        productClient;

    private final OrderEventPublisher  publisher;

    // ============================================================
    // FLOW ĐẶT HÀNG BÌNH THƯỜNG (COD + PAYOS(BANK_TRANSFER))
    // ============================================================
    public OrderResponse createOrder(Long userId, OrderCreateRequest request) {
        if (!userClient.exists(userId)) {
            throw new RuntimeException("user is invalid " + userId);
        }

        // ✅ SỬA: Cho phép COD + BANK_TRANSFER (PayOS)
        // INSTALLMENT phải đi bằng flow riêng (Kafka)
        PaymentMethod pm = request.getPaymentMethod();
        if (pm == PaymentMethod.INSTALLMENT) {
            throw new RuntimeException("INSTALLMENT orders must come from installment flow");
        }

        if (pm != PaymentMethod.COD && pm != PaymentMethod.BANK_TRANSFER) {
            throw new RuntimeException("Unsupported payment method: " + pm);
        }

        return doCreateBaseOrder(userId, request);
    }

    // ============================================================
    // FLOW ĐẶT HÀNG TỪ TRẢ GÓP (INSTALLMENT-SERVICE → KAFKA)
    // ============================================================
    /**
     * Dùng cho flow: installment-service APPROVED → gửi Kafka → order-service tạo đơn.
     * - Enforce paymentMethod = INSTALLMENT
     */
    public OrderResponse createOrderFromInstallment(Long userId, OrderCreateRequest request) {
        if (!userClient.exists(userId)) {
            throw new RuntimeException("user is invalid " + userId);
        }

        if (request.getPaymentMethod() != PaymentMethod.INSTALLMENT) {
            throw new RuntimeException("PaymentMethod must be INSTALLMENT for installment orders");
        }

        return doCreateBaseOrder(userId, request);
    }

    public OrderResponse getById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return orderMapper.toOrderResponse(order);
    }

    // ============================================================
    // CORE: TẠO ORDER + SET SNAPSHOT ITEM + PUBLISH EVENT
    // ============================================================
    private OrderResponse doCreateBaseOrder(Long userId, OrderCreateRequest request) {
        // build order từ mapper
        Order order = orderMapper.toOrder(userId, request);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(Instant.now());

        // Mã thanh toán dùng riêng cho PayOS / tracking (unique, không dùng order.id)
        // (Bạn đang set cho cả COD cũng OK nếu DB cần not-null)
        order.setPaymentCode(System.currentTimeMillis());

        // shipping address
        var address = customerClient.getAddress(request.getAddressId());
        order.setShippingAddress(address.toString());

        return setOrderItems(order, request.getItems());
    }

    /**
     * Tạo OrderItem từ snapshot + request
     * - Map snapshot theo (productId, variantId)
     * - Lưu variantId vào OrderItem
     * - Publish OrderCreatedEvent với idempotencyKey
     */
    private OrderResponse setOrderItems(Order order, List<OrderItemRequest> orderItems) {

        // 1) Gọi product-service lấy snapshot
        var requests  = orderMapper.toRequestList(orderItems);
        var snapshots = productClient.getProductsSnapshot(requests);

        // 2) Build map snapshot theo key (productId, variantId)
        Map<ItemKey, ProductSnapshotResponse> snapshotMap = snapshots.stream()
                .collect(Collectors.toMap(
                        s -> new ItemKey(s.getProductId(), s.getVariantId()),
                        s -> s,
                        (a, b) -> a
                ));

        List<OrderItem> items = new ArrayList<>();

        for (var data : orderItems) {
            ItemKey key = new ItemKey(data.getProductId(), data.getVariantId());
            ProductSnapshotResponse snapshot = snapshotMap.get(key);

            if (snapshot == null) {
                throw new IllegalStateException(
                        "Snapshot not found for productId=" + data.getProductId()
                                + ", variantId=" + data.getVariantId()
                );
            }

            OrderItem item = orderMapper.toOrderItem(snapshot);

            item.setProductId(data.getProductId());
            item.setVariantId(data.getVariantId());
            item.setQuantity(data.getQuantity());

            item.setOriginalPrice(snapshot.getPrice());
            item.setFinalPrice(
                    snapshot.getDiscountedPrice() != null
                            ? snapshot.getDiscountedPrice()
                            : snapshot.getPrice()
            );

            item.setOrder(order);
            items.add(item);
        }

        order.setItems(items);
        Order savedOrder = orderRepository.save(order);

        // 3) Publish OrderCreatedEvent (idempotencyKey thật)
        String idempotencyKey = UUID.randomUUID().toString();

        publisher.publish(new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                idempotencyKey,
                orderItems.stream()
                        .map(o -> new OrderCreatedEvent.Item(
                                o.getProductId(),
                                o.getVariantId(),
                                o.getQuantity()
                        ))
                        .toList()
        ));

        return orderMapper.toOrderResponse(savedOrder);
    }

    // record key để map snapshot
    private record ItemKey(Long productId, Long variantId) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ItemKey itemKey = (ItemKey) o;
            return Objects.equals(productId, itemKey.productId)
                    && Objects.equals(variantId, itemKey.variantId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, variantId);
        }
    }

    // ==========================
    // READ APIs
    // ==========================

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

    public OrderResponse cancelOrder(Long orderId, Long userId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUserId().equals(userId))
            throw new OrderCannotBeCancelledException("no access " + userId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new OrderCannotBeCancelledException(order.getStatus().toString());

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(cancelReason);

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    public boolean confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING)
            return false;

        order.setStatus(OrderStatus.SHIPPING);
        orderRepository.save(order);
        return true;
    }

    public boolean setOrderDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.SHIPPING)
            return false;

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        OrderDeliveredEvent deliveredEvent =
                new OrderDeliveredEvent(
                        order.getId(),
                        order.getUserId(),
                        "??",
                        order.getItems().stream().map(
                                        (i) -> new OrderDeliveredEvent.Item(
                                                i.getProductId(),
                                                i.getVariantId(),
                                                i.getQuantity(),
                                                orderItemRepository.countByVariantId(i.getVariantId()))
                                )
                                .toList());

        publisher.publishOrderDeliveredEvent(deliveredEvent);

        return true;
    }

    public boolean setOrderFailed(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED)
            return false;

        order.setStatus(OrderStatus.FAILED);
        orderRepository.save(order);
        return true;
    }

    public Page<OrderResponse> getOrdersPaged(int page, int size,
                                              OrderStatus status,
                                              PaymentMethod payment,
                                              ShippingMethod shipping) {

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

    public List<MonthlySalesResponse> getMonthlySales() {
        return orderRepository.findMonthlySales();
    }

    public List<MonthlyOrderCountResponse> getMonthlyOrderCount() {
        return orderRepository.findMonthlyOrderCount();
    }

    public List<OrderStatusCountResponse> getOrderStatusStats() {
        return orderRepository.countOrdersByStatus();
    }

    public List<BestSellingProductResponse> getBestSellingProducts() {
        return orderItemRepository.findBestSellingProducts();
    }

    public Long getTotalSoldQuantity() {
        return orderItemRepository.getTotalSoldQuantity();
    }
}
