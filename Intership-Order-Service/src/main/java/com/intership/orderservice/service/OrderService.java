package com.intership.orderservice.service;

import com.intership.orderservice.model.dto.OrderRequest;
import com.intership.orderservice.model.dto.OrderResponse;
import com.intership.orderservice.model.entity.Order;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    OrderResponse findOrderById(Long id);
    List<OrderResponse> findOrdersByIds(List<Long> ids);
    List<OrderResponse> findOrdersByStatuses(List<Order.Status> statuses);
    OrderResponse updateOrder(long id, OrderRequest order);

    void updateOrderStatus(long id, Order.Status status);

    void deleteOrder(long id);
}
