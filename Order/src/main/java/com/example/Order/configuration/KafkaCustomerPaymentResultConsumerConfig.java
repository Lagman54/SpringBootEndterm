package com.example.Order.configuration;


import com.example.Order.model.replies.CustomerPaymentResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaCustomerPaymentResultConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.customer-payment-result-group}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, CustomerPaymentResult> customerPaymentResultConsumerFactory() {
        JsonDeserializer<CustomerPaymentResult> deserializer = new JsonDeserializer<>(CustomerPaymentResult.class);
        deserializer.addTrustedPackages("*");
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

    @Bean(name = "customerPaymentResultListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, CustomerPaymentResult> customerPaymentResultListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CustomerPaymentResult> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(customerPaymentResultConsumerFactory());
        return factory;
    }

}