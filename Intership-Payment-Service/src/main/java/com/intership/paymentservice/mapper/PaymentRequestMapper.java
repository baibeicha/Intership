package com.intership.paymentservice.mapper;

import com.intership.paymentservice.model.dto.PaymentRequest;
import com.intership.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapper.class)
public interface PaymentRequestMapper extends BaseMapper<Payment, PaymentRequest> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Payment toEntity(PaymentRequest dto);

    @Override
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "paymentAmount", ignore = true)
    PaymentRequest toDto(Payment entity);
}