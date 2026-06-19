package com.intership.paymentservice.repository;

import com.intership.paymentservice.model.entity.Payment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByUserId(Long userId);

    Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId);

    List<Payment> findByStatusIn(List<Payment.Status> statuses);

    BigDecimal getTotalPaymentsAmountForPeriod(Timestamp startDate, Timestamp endDate);
}