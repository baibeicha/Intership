package com.intership.paymentservice.service.impl;

import com.intership.paymentservice.model.entity.Payment;
import com.intership.paymentservice.service.PaymentStatusService;
import com.intership.paymentservice.service.RandomNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatusServiceImpl implements PaymentStatusService {

    private final RandomNumberService randomNumberService;

    @Override
    public Payment.Status determinePaymentStatus() {
        return randomNumberService.getRandomNumber() % 2 == 0 ? Payment.Status.SUCCESS : Payment.Status.FAILED;
    }
}
