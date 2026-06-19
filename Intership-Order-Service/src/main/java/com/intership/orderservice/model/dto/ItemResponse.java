package com.intership.orderservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemResponse {
    private Long id;
    private String name;
    private BigDecimal price;
}
