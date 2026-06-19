package com.intership.paymentservice.service;

import com.intership.paymentservice.model.entity.Payment;

public interface PaymentStatusService {
    Payment.Status determinePaymentStatus();
}
