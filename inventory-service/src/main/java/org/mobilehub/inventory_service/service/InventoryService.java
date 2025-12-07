package org.mobilehub.inventory_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.inventory_service.dto.response.InventoryReservationResponse;
import org.mobilehub.inventory_service.dto.response.InventoryStockResponse;
import org.mobilehub.inventory_service.entity.InventoryReservation;
import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.mobilehub.inventory_service.entity.InventoryStock;
import org.mobilehub.inventory_service.entity.ReservationStatus;
import org.mobilehub.inventory_service.exception.InsufficientStockException;
import org.mobilehub.inventory_service.kafka.InventoryEventPublisher;
import org.mobilehub.inventory_service.mapper.InventoryMapper;
import org.mobilehub.inventory_service.repository.InventoryReservationRepository;
import org.mobilehub.inventory_service.repository.InventoryStockRepository;
import org.mobilehub.shared.common.events.InventoryCommittedEvent;
import org.mobilehub.shared.common.events.InventoryRejectedEvent;
import org.mobilehub.shared.common.events.InventoryReleasedEvent;
import org.mobilehub.shared.common.events.InventoryReservedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryStockRepository stockRepo;
    private final InventoryReservationRepository reservationRepo;
    private final InventoryMapper mapper;
    private final InventoryEventPublisher publisher;

    // ==========================
    // STOCK APIs
    // ==========================

    @Transactional(readOnly = true)
    public InventoryStockResponse getStock(Long productId) {
        InventoryStock stock = stockRepo.findByProductId(productId)
                .orElse(null);
        if(stock == null)
            return new InventoryStockResponse(0L, 0L, 0L, 0L);

        return mapper.toStockResponse(stock);
    }

    @Transactional
    public InventoryStockResponse adjustStock(Long productId, Long delta) {
        InventoryStock stock = stockRepo.findByProductId(productId)
                .orElseGet(() -> {
                    InventoryStock s = new InventoryStock();
                    s.setProductId(productId);
                    s.setOnHand(0L);
                    s.setReserved(0L);
                    return s;
                });

        long newOnHand = stock.getOnHand() + delta;
        if (newOnHand < 0) {
            throw new IllegalArgumentException("onHand cannot be negative");
        }
        if (newOnHand < stock.getReserved()) {
            throw new IllegalArgumentException("onHand cannot be less than reserved");
        }

        stock.setOnHand(newOnHand);
        stockRepo.save(stock);
        return mapper.toStockResponse(stock);
    }

    // ==========================
    // RESERVATION APIs
    // ==========================

    @Transactional
    public InventoryReservationResponse reserve(Long orderId,
                                                List<InventoryReservationItem> orderItems,
                                                String idempotencyKey) {

        // Idempotent: đã reserve rồi thì trả lại + publish lại RESERVED
        var existed = reservationRepo.findByIdempotencyKey(idempotencyKey);
        if (existed.isPresent()) {
            InventoryReservation res = existed.get();

            publisher.publishReserved(
                    res.getOrderId(),
                    res.getReservationId(),
                    res.getExpiresAt(),
                    toReservedLines(res)
            );

            return mapper.toReservationResponse(res);
        }

        // ====== (NEW) Gom request theo productId để check chuẩn ======
        Map<Long, Long> requestedByPid = orderItems.stream()
                .collect(Collectors.groupingBy(
                        InventoryReservationItem::getProductId,
                        Collectors.summingLong(InventoryReservationItem::getQuantity)
                ));

        // ===== Phase A: lock + check đủ hàng cho TẤT CẢ sản phẩm =====
        Map<Long, InventoryStock> lockedStocks = new HashMap<>();
        List<InventoryRejectedEvent.Missing> missing = new ArrayList<>();

        for (var entry : requestedByPid.entrySet()) {
            Long pid = entry.getKey();
            Long reqQty = entry.getValue();

            InventoryStock stock = stockRepo.lockByProductId(pid).orElse(null);
            long available = (stock == null) ? 0L : stock.getOnHand() - stock.getReserved();

            if (stock == null || available < reqQty) {
                // ✅ dùng Missing của shared event
                missing.add(new InventoryRejectedEvent.Missing(pid, reqQty, available));
            } else {
                lockedStocks.put(pid, stock);
            }
        }

// Nếu thiếu bất kỳ line nào -> throw kèm list thiếu
        if (!missing.isEmpty()) {
            throw new InsufficientStockException("Out of stock", missing);
        }


        // ====== Phase B: tạo reservation + tăng reserved ======
        InventoryReservation reservation = new InventoryReservation();
        reservation.setReservationId(UUID.randomUUID().toString());
        reservation.setOrderId(orderId);
        reservation.setIdempotencyKey(idempotencyKey);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpiresAt(Instant.now().plusSeconds(15 * 60));
        reservation.setItems(new ArrayList<>());

        for (InventoryReservationItem reqItem : orderItems) {
            InventoryStock stock = lockedStocks.get(reqItem.getProductId());
            // stock chắc chắn không null vì phase A đã pass

            stock.setReserved(stock.getReserved() + reqItem.getQuantity());
            stockRepo.save(stock); // (optional nhưng nên có cho rõ ràng)

            InventoryReservationItem item = new InventoryReservationItem();
            item.setProductId(reqItem.getProductId());
            item.setQuantity(reqItem.getQuantity());
            item.setReservation(reservation);

            reservation.getItems().add(item);
        }

        InventoryReservation saved = reservationRepo.save(reservation);

        publisher.publishReserved(
                saved.getOrderId(),
                saved.getReservationId(),
                saved.getExpiresAt(),
                toReservedLines(saved)
        );

        return mapper.toReservationResponse(saved);
    }

    @Transactional
    public InventoryReservationResponse commit(String reservationId) {
        InventoryReservation reservation = reservationRepo.findByReservationIdWithItems(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reservation can be committed");
        }

        for (InventoryReservationItem item : reservation.getItems()) {
            InventoryStock stock = stockRepo.lockByProductId(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Stock not found for product " + item.getProductId()
                    ));

            stock.setOnHand(stock.getOnHand() - item.getQuantity());
            stock.setReserved(stock.getReserved() - item.getQuantity());

            stockRepo.save(stock);
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        InventoryReservation saved = reservationRepo.save(reservation);

        publisher.publishCommitted(
                saved.getOrderId(),
                saved.getReservationId(),
                toCommittedLines(saved)
        );

        return mapper.toReservationResponse(saved);
    }


    @Transactional
    public InventoryReservationResponse release(String reservationId) {
        InventoryReservation reservation = reservationRepo.findByReservationIdWithItems(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            return mapper.toReservationResponse(reservation);
        }

        for (InventoryReservationItem item : reservation.getItems()) {
            InventoryStock stock = stockRepo.lockByProductId(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Stock not found for product " + item.getProductId()
                    ));

            stock.setReserved(stock.getReserved() - item.getQuantity());

            stockRepo.save(stock); // ✅ explicit save
        }

        reservation.setStatus(ReservationStatus.RELEASED);
        InventoryReservation saved = reservationRepo.save(reservation);

        publisher.publishReleased(
                saved.getOrderId(),
                saved.getReservationId(),
                toReleasedLines(saved)
        );

        return mapper.toReservationResponse(saved);
    }

    @Transactional
    public InventoryReservationResponse cancel(String reservationId) {
        InventoryReservation reservation = reservationRepo.findByReservationIdWithItems(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            // idempotent: đã final rồi thì trả luôn
            return mapper.toReservationResponse(reservation);
        }

        for (InventoryReservationItem item : reservation.getItems()) {
            InventoryStock stock = stockRepo.lockByProductId(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Stock not found for product " + item.getProductId()
                    ));

            stock.setReserved(stock.getReserved() - item.getQuantity());
            stockRepo.save(stock);
        }

        reservation.setStatus(ReservationStatus.CANCELED);
        InventoryReservation saved = reservationRepo.save(reservation);

        // ✅ Tối thiểu: publish RELEASED để Order vẫn hiểu là stock được trả
        publisher.publishReleased(
                saved.getOrderId(),
                saved.getReservationId(),
                toReleasedLines(saved)
        );

        return mapper.toReservationResponse(saved);
    }



    /** Auto release reservation hết hạn */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoReleaseExpiredReservations() {
        List<InventoryReservation> expired = reservationRepo
                .findAllByStatusAndExpiresAtBefore(ReservationStatus.PENDING, Instant.now());

        for (InventoryReservation res : expired) {
            release(res.getReservationId());
        }
    }

    // ==========================
    // HELPERS: map items -> lines
    // ==========================

    private List<InventoryReservedEvent.Line> toReservedLines(InventoryReservation reservation) {
        return reservation.getItems().stream()
                .map(i -> new InventoryReservedEvent.Line(i.getProductId(), i.getQuantity()))
                .toList();
    }

    private List<InventoryCommittedEvent.Line> toCommittedLines(InventoryReservation reservation) {
        return reservation.getItems().stream()
                .map(i -> new InventoryCommittedEvent.Line(i.getProductId(), i.getQuantity()))
                .toList();
    }

    private List<InventoryReleasedEvent.Line> toReleasedLines(InventoryReservation reservation) {
        return reservation.getItems().stream()
                .map(i -> new InventoryReleasedEvent.Line(i.getProductId(), i.getQuantity()))
                .toList();
    }
}
