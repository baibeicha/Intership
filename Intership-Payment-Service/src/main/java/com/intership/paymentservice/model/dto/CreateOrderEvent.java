package com.intership.paymentservice.model.dto;

import java.math.BigDecimal;

public record CreateOrderEvent(
        Long orderId,
        Long userId,
        BigDecimal amount
) {}