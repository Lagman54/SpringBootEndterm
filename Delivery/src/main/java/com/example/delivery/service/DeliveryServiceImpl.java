package com.example.delivery.service;

import com.example.delivery.model.DeliveryHistory;
import com.example.delivery.model.DeliverySlot;
import com.example.delivery.model.OrderDto;
import com.example.delivery.model.OutboxEvent;
import com.example.delivery.model.replies.DeliveryFailed;
import com.example.delivery.model.replies.DeliveryResult;
import com.example.delivery.model.replies.DeliverySuccess;
import com.example.delivery.repository.DeliveryHistoryRepository;
import com.example.delivery.repository.DeliverySlotRepository;
import com.example.delivery.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryServiceImpl implements DeliveryService {
    private static final Logger log = LogManager.getLogger(DeliveryServiceImpl.class);

    private final DeliverySlotRepository deliverySlotRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public DeliveryServiceImpl(
            DeliverySlotRepository deliverySlotRepository,
            DeliveryHistoryRepository deliveryHistoryRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.deliverySlotRepository    = deliverySlotRepository;
        this.deliveryHistoryRepository = deliveryHistoryRepository;
        this.outboxRepository          = outboxRepository;
        this.objectMapper              = objectMapper;
    }

    @Override
    @Transactional
    public void scheduleDelivery(OrderDto order) {
        DeliveryResult resultEvent;

        // 1) Validate address
        if (!isValidAddress(order.address())) {
            resultEvent = new DeliveryFailed(order.orderId(), order.customerId());
        } else {
            // 2) Find a free slot
            List<DeliverySlot> slots = deliverySlotRepository.findAll();
            DeliverySlot slot = slots.stream()
                    .filter(s -> s.getQuantity() < s.getMaxQuantity())
                    .findFirst()
                    .orElse(null);

            if (slot == null) {
                resultEvent = new DeliveryFailed(order.orderId(), order.customerId());
            } else {
                // 3) Reserve slot and record history
                slot.setQuantity(slot.getQuantity() + 1);
                deliverySlotRepository.save(slot);

                var history = new DeliveryHistory();
                history.setOrderId(order.orderId());
                history.setCustomerId(order.customerId());
                history.setSlot(slot);
                history.setStartTime(slot.getStartTime());
                history.setEndTime(slot.getEndTime());
                deliveryHistoryRepository.save(history);

                log.info("Scheduled delivery for order {} customer {}", order.orderId(), order.customerId());
                resultEvent = new DeliverySuccess(order.orderId(), order.customerId());
            }
        }

        // 4) Create & save outbox record
        try {
            OutboxEvent ev = toOutbox(
                    resultEvent,
                    resultEvent instanceof DeliverySuccess
                            ? "delivery.success"
                            : "delivery.failed",
                    order.orderId()
            );
            outboxRepository.save(ev);
        } catch (JsonProcessingException e) {
            // any exception here will roll back the whole transaction
            log.error("Failed to enqueue delivery result for order {}", order.orderId(), e);
            throw new RuntimeException(e);
        }


    }



    private boolean isValidAddress(String address) {
        return address != null &&
                !address.trim().isEmpty() &&
                address.length() < 255 &&
                address.matches("[a-zA-Z0-9 ,.\\-]+");
    }

    private OutboxEvent toOutbox(Object payloadObj, String type, Long aggregateId)
            throws JsonProcessingException {
        OutboxEvent ev = new OutboxEvent();
        ev.setId(UUID.randomUUID());
        ev.setAggregateType("delivery");
        ev.setAggregateId(aggregateId.toString());
        ev.setType(type);
        ev.setPayload(objectMapper.writeValueAsString(payloadObj));
        ev.setCreatedAt(LocalDateTime.now());
        ev.setSent(false);
        return ev;
    }
}