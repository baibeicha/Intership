package com.intership.orderservice.controller;

import com.intership.orderservice.model.dto.OrderRequest;
import com.intership.orderservice.model.dto.OrderResponse;
import com.intership.orderservice.model.entity.Order;
import com.intership.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(201).body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.findOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findOrders(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "statuses", required = false) List<String> statusesStr) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.ok(orderService.findOrdersByIds(ids));
        }

        if (statusesStr != null && !statusesStr.isEmpty()) {
            List<Order.Status> statuses = statusesStr.stream()
                    .map(s -> Order.Status.valueOf(s.trim().toUpperCase()))
                    .toList();
            return ResponseEntity.ok(orderService.findOrdersByStatuses(statuses));
        }

        throw new IllegalArgumentException("Provide 'ids' or 'statuses' query parameter");
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
