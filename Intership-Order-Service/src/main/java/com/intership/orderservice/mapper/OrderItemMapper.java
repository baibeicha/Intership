package com.intership.orderservice.mapper;

import com.intership.orderservice.model.dto.OrderItemRequest;
import com.intership.orderservice.model.dto.OrderItemResponse;
import com.intership.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(config = BaseMapper.class)
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemResponse> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "item", ignore = true)
    OrderItem toEntityFromRequest(OrderItemRequest orderItemRequestDTO);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "itemName", source = "item.name")
    @Mapping(target = "itemPrice", source = "item.price")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(orderItem))")
    OrderItemResponse toDto(OrderItem orderItem);

    default BigDecimal calculateTotalPrice(OrderItem orderItem) {
        if (orderItem.getItem() == null || orderItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return orderItem.getItem().getPrice().multiply(
                BigDecimal.valueOf(orderItem.getQuantity())
        );
    }
}