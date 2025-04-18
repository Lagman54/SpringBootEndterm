package com.example.Order.configuration.consumer;

import com.example.Order.model.replies.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDeliveryResultConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.delivery-result-group}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, DeliveryResult> deliveryResultConsumerFactory() {
        JsonDeserializer<DeliveryResult> deserializer = new JsonDeserializer<>(DeliveryResult.class);
        deserializer.addTrustedPackages("*");
        deserializer.setTypeMapper(new CustomTypeMapper());
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, groupId,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "deliveryResultListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryResult> deliveryResultListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryResult> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryResultConsumerFactory());
        return factory;
    }

    public class CustomTypeMapper extends DefaultJackson2JavaTypeMapper {

        public CustomTypeMapper() {
            Map<String, Class<?>> idClassMapping = new HashMap<>();
            idClassMapping.put("com.example.delivery.model.replies.DeliverySuccess", DeliverySuccess.class);
            idClassMapping.put("com.example.delivery.model.replies.DeliveryFailed", DeliveryFailed.class);

            setIdClassMapping(idClassMapping);
            setTypePrecedence(TypePrecedence.TYPE_ID);
        }
    }
}