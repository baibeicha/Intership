package com.intership.paymentservice.service.impl;

import com.intership.paymentservice.exception.PaymentNotFoundException;
import com.intership.paymentservice.mapper.PaymentRequestMapper;
import com.intership.paymentservice.mapper.PaymentResponseMapper;
import com.intership.paymentservice.model.dto.PaymentRequest;
import com.intership.paymentservice.model.dto.PaymentResponse;
import com.intership.paymentservice.model.entity.Payment;
import com.intership.paymentservice.repository.PaymentRepository;
import com.intership.paymentservice.service.PaymentService;
import com.intership.paymentservice.service.PaymentStatusService;
import com.intership.paymentservice.service.kafka.producer.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentRequestMapper paymentRequestMapper;
    private final PaymentResponseMapper paymentResponseMapper;
    private final PaymentStatusService paymentStatusService;
    private final PaymentEventProducer paymentProducer;

    @Transactional
    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        Payment payment = paymentRequestMapper.toEntity(paymentRequest);
        payment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        payment.setStatus(paymentStatusService.determinePaymentStatus());

        payment = paymentRepository.save(payment);

        PaymentResponse response = paymentResponseMapper.toDto(payment);
        paymentProducer.sendPaymentEvent(response);
        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentResponse getPaymentByIds(Long orderId, Long userId) {
        return paymentResponseMapper.toDto(findByIds(orderId, userId));
    }

    @Transactional
    @Override
    public PaymentResponse updatePaymentByIds(PaymentRequest paymentRequest) {
        Payment payment = findByIds(paymentRequest.orderId(), paymentRequest.userId());
        payment = paymentRequestMapper.merge(payment, paymentRequest);
        payment = paymentRepository.save(payment);
        return paymentResponseMapper.toDto(payment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return paymentResponseMapper.toDtos(payments);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return paymentResponseMapper.toDtos(payments);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentResponse> getPaymentsByStatuses(List<Payment.Status> statuses) {
        List<Payment> payments = paymentRepository.findByStatusIn(statuses);
        return paymentResponseMapper.toDtos(payments);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal calculateAllPaymentsBetween(Timestamp startDate, Timestamp endDate) {
        return paymentRepository.getTotalPaymentsAmountForPeriod(startDate, endDate);
    }

    private Payment findByIds(Long orderId, Long userId) {
        return paymentRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment with order id " + orderId + " and user id " + userId + "not found"
                        )
                );
    }
}