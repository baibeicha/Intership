package com.intership.orderservice.mapper;

import com.intership.orderservice.model.dto.OrderRequest;
import com.intership.orderservice.model.dto.OrderResponse;
import com.intership.orderservice.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class, uses = OrderItemMapper.class)
public interface OrderMapper extends BaseMapper<Order, OrderResponse> {

    @Override
    @Mapping(target = "userInfo", ignore = true)
    OrderResponse toDto(Order entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "orderItems", ignore = true)
    Order toEntityFromRequest(OrderRequest orderRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    void updateOrderFromRequest(@MappingTarget Order order, OrderRequest orderRequestDTO);
}
