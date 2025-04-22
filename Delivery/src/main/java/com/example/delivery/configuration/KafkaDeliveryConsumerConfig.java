package com.example.delivery.configuration;


import com.example.delivery.model.OrderDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaDeliveryConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.delivery-created-consumers}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, OrderDto> createDeliveryConsumerFactory() {
        JsonDeserializer<OrderDto> deserializer = new JsonDeserializer<>(OrderDto.class);

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, groupId,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                        JsonDeserializer.VALUE_DEFAULT_TYPE, OrderDto.class.getName(),
                        JsonDeserializer.USE_TYPE_INFO_HEADERS, false
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "createDeliveryListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OrderDto> createDeliveryListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createDeliveryConsumerFactory());
        return factory;
    }


}