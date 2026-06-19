package com.intership.paymentservice.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest (

    @NotNull(message = "Order ID cannot be null")
    @Positive(message = "Order ID must be positive")
    Long orderId,

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    Long userId,

    @NotNull(message = "Payment amount cannot be null")
    @DecimalMin(value = "0.01", message = "Payment amount must be at least 0.01")
    BigDecimal paymentAmount
) {}