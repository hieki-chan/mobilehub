package org.mobilehub.inventory_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.shared.common.events.InventoryCommittedEvent;
import org.mobilehub.shared.common.events.InventoryRejectedEvent;
import org.mobilehub.shared.common.events.InventoryReleasedEvent;
import org.mobilehub.shared.common.events.InventoryReservedEvent;
import org.mobilehub.shared.common.topics.Topics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaInventoryEventPublisher implements InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishReserved(Long orderId, String reservationId, Instant expiresAt,
                                List<InventoryReservedEvent.Line> lines) {

        InventoryReservedEvent evt = new InventoryReservedEvent(
                UUID.randomUUID().toString(),
                orderId,
                reservationId,
                expiresAt,
                lines
        );

        kafkaTemplate.send(Topics.INVENTORY_RESERVED, String.valueOf(orderId), evt);
        log.info("[inventory] Published INVENTORY_RESERVED orderId={}, reservationId={}, lines={}",
                orderId, reservationId, lines.size());
    }

    @Override
    public void publishCommitted(Long orderId, String reservationId,
                                 List<InventoryCommittedEvent.Line> lines) {

        InventoryCommittedEvent evt = new InventoryCommittedEvent(
                UUID.randomUUID().toString(),
                orderId,
                reservationId,
                lines
        );

        kafkaTemplate.send(Topics.INVENTORY_COMMITTED, String.valueOf(orderId), evt);
        log.info("[inventory] Published INVENTORY_COMMITTED orderId={}, reservationId={}, lines={}",
                orderId, reservationId, lines.size());
    }

    @Override
    public void publishReleased(Long orderId, String reservationId,
                                List<InventoryReleasedEvent.Line> lines) {

        InventoryReleasedEvent evt = new InventoryReleasedEvent(
                UUID.randomUUID().toString(),
                orderId,
                reservationId,
                lines
        );

        kafkaTemplate.send(Topics.INVENTORY_RELEASED, String.valueOf(orderId), evt);
        log.info("[inventory] Published INVENTORY_RELEASED orderId={}, reservationId={}, lines={}",
                orderId, reservationId, lines.size());
    }

    @Override
    public void publishRejected(Long orderId, String reason,
                                List<InventoryRejectedEvent.Missing> missing) {

        InventoryRejectedEvent evt = new InventoryRejectedEvent(orderId, reason, missing);


        kafkaTemplate.send(Topics.INVENTORY_REJECTED, String.valueOf(orderId), evt);
        log.info("[inventory] Published INVENTORY_REJECTED orderId={}, reason={}, missing={}",
                orderId, reason, missing.size());
    }
}
