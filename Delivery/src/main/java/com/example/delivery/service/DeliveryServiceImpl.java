package com.example.delivery.service;

import com.example.delivery.model.OrderDto;
import com.example.delivery.model.replies.DeliveryFailed;
import com.example.delivery.model.replies.DeliveryResult;
import com.example.delivery.model.replies.DeliverySuccess;
import com.example.delivery.repository.DeliveryHistoryRepository;
import com.example.delivery.repository.DeliverySlotRepository;
import com.example.delivery.model.DeliverySlot;
import com.example.delivery.model.DeliveryHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Value("${app.kafka.delivery-result-topic}")
    private String resultTopic;

    private final KafkaTemplate<String, DeliveryResult> kafkaTemplate;
    private final DeliverySlotRepository deliverySlotRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;

    public DeliveryServiceImpl(KafkaTemplate<String, DeliveryResult> kafkaTemplate,
                               DeliverySlotRepository deliverySlotRepository,
                               DeliveryHistoryRepository deliveryHistoryRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.deliverySlotRepository = deliverySlotRepository;
        this.deliveryHistoryRepository = deliveryHistoryRepository;
    }

    private boolean isValidAddress(String address) {
        return address != null &&
                !address.trim().isEmpty() &&
                address.length() < 255 &&
                address.matches("[a-zA-Z0-9 ,.-]+");
    }

    @Override
    @Transactional
    public void scheduleDelivery(OrderDto order) {
        // Validate address
        if (!isValidAddress(order.address())) {
            kafkaTemplate.send(resultTopic, new DeliveryFailed(order.orderId(), order.customerId()));
            return;
        }

        // Find available slot
        List<DeliverySlot> slots = deliverySlotRepository.findAll();
        DeliverySlot availableSlot = null;

        for (DeliverySlot slot : slots) {
            if (slot.getQuantity() < slot.getMaxQuantity()) {
                availableSlot = slot;
                break;
            }
        }

        // No slot available
        if (availableSlot == null) {
            kafkaTemplate.send(resultTopic, new DeliveryFailed(order.orderId(), order.customerId()));
            return;
        }

        // Slot found, increment quantity
        availableSlot.setQuantity(availableSlot.getQuantity() + 1);
        deliverySlotRepository.save(availableSlot);

        // Save delivery history clearly using your existing OrderDto record
        DeliveryHistory history = new DeliveryHistory();
        history.setOrderId(order.orderId());
        history.setCustomerId(order.customerId());
        history.setSlotId(availableSlot.getId());
        history.setStartTime(availableSlot.getStartTime());
        history.setEndTime(availableSlot.getEndTime());


        deliveryHistoryRepository.save(history);

        kafkaTemplate.send(resultTopic, new DeliverySuccess(order.orderId(), order.customerId()));
    }
}
