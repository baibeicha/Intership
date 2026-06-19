package com.intership.orderservice.model.dto;

import com.intership.orderservice.model.entity.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String userEmail;
    private Order.Status status;
    private LocalDateTime creationDate;
    private List<OrderItemResponse> orderItems = new ArrayList<>();
    private UserResponse userInfo;
}
