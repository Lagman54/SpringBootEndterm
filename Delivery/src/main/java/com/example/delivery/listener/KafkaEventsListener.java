package com.example.delivery.listener;

import com.example.delivery.model.OrderDto;
import com.example.delivery.service.DeliveryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class KafkaEventsListener {
    private static final Logger log = LogManager.getLogger(KafkaEventsListener.class);

    private final DeliveryService deliveryService;

    public KafkaEventsListener(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @KafkaListener(topics = "${app.kafka.delivery-created-topic}",
            groupId = "${app.kafka.delivery-created-consumers}",
            containerFactory = "createDeliveryListenerFactory"
    )
    public void listen(OrderDto order) {
        log.info("Received order: {}", order);
        deliveryService.scheduleDelivery(order);
    }

}
