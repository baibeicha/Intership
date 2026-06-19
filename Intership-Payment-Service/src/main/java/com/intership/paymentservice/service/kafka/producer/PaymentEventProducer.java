package com.intership.paymentservice.service.kafka.producer;

import com.intership.paymentservice.model.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    @Value("${topic.create-payment}")
    private String CREATE_PAYMENT_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentEvent(PaymentResponse event) {
        kafkaTemplate.send(CREATE_PAYMENT_TOPIC, event);
    }
}