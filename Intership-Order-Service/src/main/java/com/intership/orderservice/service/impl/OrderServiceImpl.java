package com.intership.orderservice.service.impl;

import com.intership.orderservice.exception.OrderNotFoundException;
import com.intership.orderservice.mapper.OrderMapper;
import com.intership.orderservice.model.dto.CreateOrderEvent;
import com.intership.orderservice.model.dto.OrderItemResponse;
import com.intership.orderservice.model.dto.OrderRequest;
import com.intership.orderservice.model.dto.OrderResponse;
import com.intership.orderservice.model.dto.UserResponse;
import com.intership.orderservice.model.entity.Order;
import com.intership.orderservice.repository.OrderRepository;
import com.intership.orderservice.service.OrderItemService;
import com.intership.orderservice.service.OrderService;
import com.intership.orderservice.service.UserService;
import com.intership.orderservice.service.kafka.producer.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = orderMapper.toEntityFromRequest(orderRequest);

        orderRequest.getOrderItems().stream()
                .map(orderItemService::toOrderItem)
                .forEach(order::addOrderItem);

        order = orderRepository.save(order);

        OrderResponse response = setUserInfoIntoOrder(orderMapper.toDto(order));
        orderEventProducer.sendCreateOrderEvent(mapToOrderEvent(response));
        return response;
    }

    private CreateOrderEvent mapToOrderEvent(OrderResponse orderResponse) {
        BigDecimal total = orderResponse.getOrderItems().stream()
                .map(OrderItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CreateOrderEvent(
                orderResponse.getId(), orderResponse.getUserInfo().getId(), total
        );
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse findOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .map(this::setUserInfoIntoOrder)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOrdersByIds(List<Long> ids) {
        List<OrderResponse> orders = orderRepository.findByIdIn(ids).stream()
                .map(orderMapper::toDto)
                .toList();
        return setUserInfoIntoOrders(orders);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findOrdersByStatuses(List<Order.Status> statuses) {
        List<OrderResponse> orders = orderRepository.findByStatusIn(statuses).stream()
                .map(orderMapper::toDto)
                .toList();
        return setUserInfoIntoOrders(orders);
    }

    @Transactional
    @Override
    public OrderResponse updateOrder(long id, OrderRequest orderRequest) {
        int updated = orderRepository.updateById(id, orderRequest.getUserEmail(), orderRequest.getStatus());
        if (updated == 0) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }

        return findOrderById(id);
    }

    @Transactional
    @Override
    public void updateOrderStatus(long id, Order.Status status) {
        int updated = orderRepository.updateStatusById(id, status);
        if (updated == 0) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
    }

    @Transactional
    @Override
    public void deleteOrder(long id) {
        orderRepository.deleteById(id);
    }

    private OrderResponse setUserInfoIntoOrder(OrderResponse orderResponse) {
        String email = orderResponse.getUserEmail();
        orderResponse.setUserInfo(userService.getUserByEmail(email));
        return orderResponse;
    }

    private List<OrderResponse> setUserInfoIntoOrders(List<OrderResponse> orderResponses) {
        List<String> emails = orderResponses.stream()
                .map(OrderResponse::getUserEmail)
                .toList();
        Map<String, UserResponse> users = userService.getUsersByEmails(emails);

        orderResponses.forEach(orderResponse -> orderResponse.setUserInfo(users.get(orderResponse.getUserEmail())));

        return orderResponses;
    }
}
