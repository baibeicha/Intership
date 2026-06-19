package com.intership.orderservice.model.dto;

import java.math.BigDecimal;

public record CreateOrderEvent(
        Long orderId,
        Long userId,
        BigDecimal amount
) {}