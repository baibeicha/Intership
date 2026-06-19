package com.intership.paymentservice;

import com.intership.paymentservice.model.entity.Payment;
import com.intership.paymentservice.service.RandomNumberService;
import com.intership.paymentservice.service.impl.PaymentStatusServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStatusServiceImplUnitTest {

    @Mock
    private RandomNumberService randomNumberService;

    @InjectMocks
    private PaymentStatusServiceImpl statusService;

    @Test
    void determinePaymentStatus_even_returnsSuccess() {
        when(randomNumberService.getRandomNumber()).thenReturn(2);

        Payment.Status status = statusService.determinePaymentStatus();

        assertThat(status).isEqualTo(Payment.Status.SUCCESS);
    }

    @Test
    void determinePaymentStatus_odd_returnsFailed() {
        when(randomNumberService.getRandomNumber()).thenReturn(3);

        Payment.Status status = statusService.determinePaymentStatus();

        assertThat(status).isEqualTo(Payment.Status.FAILED);
    }
}
