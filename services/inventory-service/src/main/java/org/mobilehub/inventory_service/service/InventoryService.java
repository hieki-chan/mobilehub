package org.mobilehub.inventory_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.inventory_service.dto.response.InventoryReservationResponse;
import org.mobilehub.inventory_service.dto.response.InventoryStockResponse;
import org.mobilehub.inventory_service.entity.InventoryReservation;
import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.mobilehub.inventory_service.entity.InventoryStock;
import org.mobilehub.inventory_service.entity.ReservationStatus;
import org.mobilehub.inventory_service.mapper.InventoryMapper;
import org.mobilehub.inventory_service.repository.InventoryReservationItemRepository;
import org.mobilehub.inventory_service.repository.InventoryReservationRepository;
import org.mobilehub.inventory_service.repository.InventoryStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service trung tâm xử lý nghiệp vụ tồn kho:
 * - Xem tồn
 * - Giữ hàng (reserve)
 * - Xác nhận thanh toán (commit)
 * - Hoàn hàng (release)
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryStockRepository stockRepo;
    private final InventoryReservationRepository reservationRepo;
    private final InventoryReservationItemRepository itemRepo;
    private final InventoryMapper mapper;

    // ==========================
    // STOCK APIs
    // ==========================

    /** Lấy thông tin tồn kho của 1 sản phẩm */
    public InventoryStockResponse getStock(Long productId) {
        InventoryStock stock = stockRepo.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return mapper.toStockResponse(stock);
    }

    /** Nhập hoặc xuất kho (admin / seed) */
    @Transactional
    public InventoryStockResponse adjustStock(Long productId, long delta) {
        InventoryStock stock = stockRepo.findByProductId(productId)
                .orElseGet(() -> {
                    InventoryStock s = new InventoryStock();
                    s.setProductId(productId);
                    s.setOnHand(0L);
                    s.setReserved(0L);
                    return s;
                });
        stock.setOnHand(stock.getOnHand() + delta);
        stockRepo.save(stock);
        return mapper.toStockResponse(stock);
    }

    // ==========================
    // RESERVATION APIs
    // ==========================

    /** Tạo giữ hàng tạm khi order.created */
    @Transactional
    public InventoryReservationResponse reserve(Long orderId, List<InventoryReservationItem> orderItems, String idempotencyKey) {
        // Check duplicate idempotent
        if (reservationRepo.findByIdempotencyKey(idempotencyKey).isPresent()) {
            return mapper.toReservationResponse(
                    reservationRepo.findByIdempotencyKey(idempotencyKey).get());
        }

        InventoryReservation reservation = new InventoryReservation();
        reservation.setReservationId(UUID.randomUUID().toString());
        reservation.setOrderId(orderId);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setIdempotencyKey(idempotencyKey);
        reservation.setExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES));

        for (InventoryReservationItem orderItem : orderItems) {
            InventoryStock stock = stockRepo.lockByProductId(orderItem.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            long available = stock.getOnHand() - stock.getReserved();
            if (available < orderItem.getQuantity()) {
                throw new IllegalArgumentException("Out of stock for product " + orderItem.getProductId());
            }

            // giữ hàng
            stock.setReserved(stock.getReserved() + orderItem.getQuantity());

            // liên kết item với reservation
            InventoryReservationItem item = new InventoryReservationItem();
            item.setProductId(orderItem.getProductId());
            item.setQuantity(orderItem.getQuantity());
            item.setReservation(reservation);
            reservation.getItems().add(item);
        }

        reservationRepo.save(reservation);
        return mapper.toReservationResponse(reservation);
    }

    /** Xác nhận thanh toán thành công → trừ kho thật */
    @Transactional
    public InventoryReservationResponse commit(String reservationId) {
        InventoryReservation reservation = reservationRepo.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CONFIRMED)
            return mapper.toReservationResponse(reservation);

        for (InventoryReservationItem item : reservation.getItems()) {
            InventoryStock stock = stockRepo.lockByProductId(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Stock not found"));
            stock.setOnHand(stock.getOnHand() - item.getQuantity());
            stock.setReserved(stock.getReserved() - item.getQuantity());
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepo.save(reservation);
        return mapper.toReservationResponse(reservation);
    }

    /** Thanh toán thất bại hoặc hủy đơn → trả hàng lại */
    @Transactional
    public InventoryReservationResponse release(String reservationId) {
        InventoryReservation reservation = reservationRepo.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING)
            return mapper.toReservationResponse(reservation);

        for (InventoryReservationItem item : reservation.getItems()) {
            InventoryStock stock = stockRepo.lockByProductId(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Stock not found"));
            stock.setReserved(stock.getReserved() - item.getQuantity());
        }

        reservation.setStatus(ReservationStatus.RELEASED);
        reservationRepo.save(reservation);
        return mapper.toReservationResponse(reservation);
    }

    /** Cron job / Scheduler: tự động release các reservation hết hạn */
    @Transactional
    public void autoReleaseExpiredReservations() {
        List<InventoryReservation> expired = reservationRepo
                .findAllByStatusAndExpiresAtBefore(ReservationStatus.PENDING, Instant.now());

        for (InventoryReservation res : expired) {
            release(res.getReservationId());
        }
    }
}
