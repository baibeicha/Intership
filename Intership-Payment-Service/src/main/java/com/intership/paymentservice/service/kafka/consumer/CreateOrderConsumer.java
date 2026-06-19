package com.intership.paymentservice.service.kafka.consumer;

import com.intership.paymentservice.model.dto.CreateOrderEvent;
import com.intership.paymentservice.model.dto.PaymentRequest;
import com.intership.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "${topic.create-order}")
    public void handleCreateOrderEvent(CreateOrderEvent event) {
        log.info("Received CREATE_ORDER event for order: {}, user: {}", event.orderId(), event.userId());

        paymentService.processPayment(new PaymentRequest(
                event.orderId(),
                event.userId(),
                event.amount()
        ));

        log.info("Successfully processed payment for order: {}", event.orderId());
    }
}