package com.intership.orderservice.service;

import com.intership.orderservice.model.dto.OrderItemRequest;
import com.intership.orderservice.model.entity.OrderItem;

public interface OrderItemService {
    OrderItem toOrderItem(OrderItemRequest request);
}
