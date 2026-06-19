package com.intership.paymentservice.service;

import com.intership.paymentservice.model.dto.PaymentRequest;
import com.intership.paymentservice.model.dto.PaymentResponse;
import com.intership.paymentservice.model.entity.Payment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentByIds(Long orderId, Long userId);

    PaymentResponse updatePaymentByIds(PaymentRequest paymentRequest);

    List<PaymentResponse> getPaymentsByOrderId(Long orderId);

    List<PaymentResponse> getPaymentsByUserId(Long userId);

    List<PaymentResponse> getPaymentsByStatuses(List<Payment.Status> statuses);

    BigDecimal calculateAllPaymentsBetween(Timestamp startDate, Timestamp endDate);
}