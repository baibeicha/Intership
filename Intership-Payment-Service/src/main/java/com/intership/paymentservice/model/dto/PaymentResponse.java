package com.intership.paymentservice.model.dto;

import com.intership.paymentservice.model.entity.Payment;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record PaymentResponse (
    Long orderId,
    Long userId,
    Payment.Status status,
    Timestamp timestamp,
    BigDecimal paymentAmount
) {}