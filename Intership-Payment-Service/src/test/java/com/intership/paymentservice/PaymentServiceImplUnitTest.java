package com.intership.paymentservice;

import com.intership.paymentservice.exception.PaymentNotFoundException;
import com.intership.paymentservice.mapper.PaymentRequestMapper;
import com.intership.paymentservice.mapper.PaymentResponseMapper;
import com.intership.paymentservice.model.dto.PaymentRequest;
import com.intership.paymentservice.model.dto.PaymentResponse;
import com.intership.paymentservice.model.entity.Payment;
import com.intership.paymentservice.repository.PaymentRepository;
import com.intership.paymentservice.service.PaymentStatusService;
import com.intership.paymentservice.service.impl.PaymentServiceImpl;
import com.intership.paymentservice.service.kafka.producer.PaymentEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentRequestMapper paymentRequestMapper;

    @Mock
    private PaymentResponseMapper paymentResponseMapper;

    @Mock
    private PaymentStatusService paymentStatusService;

    @Mock
    private PaymentEventProducer paymentProducer;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    private PaymentRequest request;
    private Payment entity;
    private PaymentResponse response;

    @BeforeEach
    void setUp() {
        request = new PaymentRequest(1L, 2L, new BigDecimal("10.00"));
        entity = new Payment();
        entity.setOrderId(1L);
        entity.setUserId(2L);
        entity.setPaymentAmount(new BigDecimal("10.00"));

        response = new PaymentResponse(1L, 2L, Payment.Status.SUCCESS, null, new BigDecimal("10.00"));
    }

    @Test
    void processPayment_savesAndEmitsEvent() {
        when(paymentRequestMapper.toEntity(request)).thenReturn(entity);
        when(paymentStatusService.determinePaymentStatus()).thenReturn(Payment.Status.SUCCESS);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentResponseMapper.toDto(any(Payment.class))).thenReturn(response);

        PaymentResponse result = paymentService.processPayment(request);

        verify(paymentRepository).save(paymentCaptor.capture());
        Payment saved = paymentCaptor.getValue();

        assertThat(saved.getTimestamp()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(Payment.Status.SUCCESS);
        assertThat(result).isEqualTo(response);
        verify(paymentProducer).sendPaymentEvent(response);
    }

    @Test
    void getPaymentByIds_returnsDto() {
        when(paymentRepository.findByOrderIdAndUserId(1L, 2L)).thenReturn(Optional.of(entity));
        when(paymentResponseMapper.toDto(entity)).thenReturn(response);

        PaymentResponse result = paymentService.getPaymentByIds(1L, 2L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getPaymentByIds_notFound_throws() {
        when(paymentRepository.findByOrderIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByIds(1L, 2L))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void updatePaymentByIds_mergesAndSaves() {
        when(paymentRepository.findByOrderIdAndUserId(1L, 2L)).thenReturn(Optional.of(entity));
        when(paymentRequestMapper.merge(entity, request)).thenReturn(entity);
        when(paymentRepository.save(entity)).thenReturn(entity);
        when(paymentResponseMapper.toDto(entity)).thenReturn(response);

        PaymentResponse result = paymentService.updatePaymentByIds(request);

        verify(paymentRepository).save(entity);
        assertThat(result).isEqualTo(response);
    }

    @Test
    void getPaymentsByOrderId_mapsList() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(List.of(entity));
        when(paymentResponseMapper.toDtos(List.of(entity))).thenReturn(List.of(response));

        List<PaymentResponse> results = paymentService.getPaymentsByOrderId(1L);

        assertThat(results).containsExactly(response);
    }

    @Test
    void getPaymentsByUserId_mapsList() {
        when(paymentRepository.findByUserId(2L)).thenReturn(List.of(entity));
        when(paymentResponseMapper.toDtos(List.of(entity))).thenReturn(List.of(response));

        List<PaymentResponse> results = paymentService.getPaymentsByUserId(2L);

        assertThat(results).containsExactly(response);
    }

    @Test
    void getPaymentsByStatuses_mapsList() {
        when(paymentRepository.findByStatusIn(List.of(Payment.Status.SUCCESS))).thenReturn(List.of(entity));
        when(paymentResponseMapper.toDtos(List.of(entity))).thenReturn(List.of(response));

        List<PaymentResponse> results = paymentService.getPaymentsByStatuses(List.of(Payment.Status.SUCCESS));

        assertThat(results).containsExactly(response);
    }

    @Test
    void calculateAllPaymentsBetween_forwardsToRepository() {
        Timestamp start = Timestamp.valueOf("2020-01-01 00:00:00");
        Timestamp end = Timestamp.valueOf("2020-01-02 00:00:00");

        when(paymentRepository.getTotalPaymentsAmountForPeriod(start, end)).thenReturn(new BigDecimal("100.00"));

        BigDecimal total = paymentService.calculateAllPaymentsBetween(start, end);

        assertThat(total).isEqualByComparingTo(new BigDecimal("100.00"));
    }
}
