package com.intership.orderservice.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
}
