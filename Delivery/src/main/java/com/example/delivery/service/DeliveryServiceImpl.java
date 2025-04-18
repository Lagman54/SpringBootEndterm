package com.example.delivery.service;

import com.example.delivery.model.OrderDto;
import com.example.delivery.model.replies.DeliveryFailed;
import com.example.delivery.model.replies.DeliveryResult;
import com.example.delivery.model.replies.DeliverySuccess;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Value("${app.kafka.delivery-result-topic}")
    private String resultTopic;

    private final KafkaTemplate<String, DeliveryResult> kafkaTemplate;

    public DeliveryServiceImpl(KafkaTemplate<String, DeliveryResult> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void scheduleDelivery(OrderDto order) {
        /* TODO
            Validate order's address, check if it's not empty, its length is less than 255 characters and doesnt contain invalid characters
            If validation fails:
                kafkaTemplate.send(resultTopic, new DeliveryFailed(order.orderId(), order.customerId()));
            else:
                Iterate through time slots in delivery_slots table and find first available slot (you can just use for() cycle to check all slots).
                If there are no available slots:
                    kafkaTemplate.send(resultTopic, new DeliveryFailed(order.orderId(), order.customerId()));
                Else if everything is ok:
                    delivery_slot.quantity += 1
                    save this delivery to delivery_history table
                    kafkaTemplate.send(resultTopic, new DeliverySuccess(order.orderId(), order.customerId()));
         */

        //for testing purposes
        kafkaTemplate.send(resultTopic, new DeliverySuccess(order.orderId(), order.customerId()));
    }
}
