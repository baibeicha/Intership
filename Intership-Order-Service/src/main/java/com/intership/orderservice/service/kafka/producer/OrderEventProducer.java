package com.intership.orderservice.service.kafka.producer;

import com.intership.orderservice.model.dto.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    @Value("${topic.create-order}")
    private String CREATE_ORDER_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreateOrderEvent(CreateOrderEvent event) {
        kafkaTemplate.send(CREATE_ORDER_TOPIC, event);
    }
}