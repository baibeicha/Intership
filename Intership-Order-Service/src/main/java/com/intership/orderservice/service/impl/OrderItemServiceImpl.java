package com.intership.orderservice.service.impl;

import com.intership.orderservice.model.dto.OrderItemRequest;
import com.intership.orderservice.model.entity.Item;
import com.intership.orderservice.model.entity.OrderItem;
import com.intership.orderservice.service.ItemService;
import com.intership.orderservice.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final ItemService itemService;

    @Transactional(readOnly = true)
    @Override
    public OrderItem toOrderItem(OrderItemRequest request) {
        Item item = itemService.getById(request.getItemId());
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setQuantity(request.getQuantity());
        return orderItem;
    }
}
