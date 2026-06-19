package com.intership.orderservice.service.kafka.consumer;

import com.intership.orderservice.model.dto.PaymentResponse;
import com.intership.orderservice.model.entity.Order;
import com.intership.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePaymentConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "${topic.create-payment}")
    public void handleCreatePaymentEvent(PaymentResponse event) {
        log.info("Received CREATE_PAYMENT event for order: {}, status: {}", event.orderId(), event.status());

        orderService.updateOrderStatus(event.orderId(), Order.Status.valueOf(event.status()));

        log.info("Successfully updated order status for order: {}", event.orderId());
    }
}