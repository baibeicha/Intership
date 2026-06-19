package com.intership.orderservice.model.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record PaymentResponse(
    Long orderId,
    Long userId,
    String status,
    Timestamp timestamp,
    BigDecimal paymentAmount
) {}