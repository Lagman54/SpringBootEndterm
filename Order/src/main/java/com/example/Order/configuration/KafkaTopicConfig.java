package com.example.Order.configuration;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.orders-topic}")
    private String ordersTopic;

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(ordersTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
