package com.intership.paymentservice.mapper;

import com.intership.paymentservice.model.dto.PaymentResponse;
import com.intership.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface PaymentResponseMapper extends BaseMapper<Payment, PaymentResponse> {
}