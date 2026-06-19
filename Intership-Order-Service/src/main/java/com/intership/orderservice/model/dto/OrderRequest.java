package com.intership.orderservice.model.dto;

import com.intership.orderservice.model.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "User email is mandatory")
    private String userEmail;

    @NotNull(message = "Status is mandatory")
    private Order.Status status;

    private List<OrderItemRequest> orderItems = new ArrayList<>();
}
